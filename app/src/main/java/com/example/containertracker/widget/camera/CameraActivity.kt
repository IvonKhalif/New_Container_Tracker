package com.example.containertracker.widget.camera

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.hardware.display.DisplayManager
import android.os.*
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.extensions.ExtensionMode
import androidx.camera.extensions.ExtensionsManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.containertracker.R
import com.example.containertracker.databinding.ActivityCameraBinding
import com.example.containertracker.utils.extension.setFullScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutionException
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates

/**
 * Created by yovi.putra on 06/04/23"
 * Project name: Container Tracker
 **/


open class CameraActivity : AppCompatActivity() {

    // An instance for display manager to get display change callbacks
    private val displayManager by lazy { getSystemService(Context.DISPLAY_SERVICE) as DisplayManager }

    // An instance of a helper function to work with Shared Preferences
    private val prefs by lazy { SharedPrefsManager.newInstance(this) }

    private var preview: Preview? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null

    // A lazy instance of the current fragment's view binding
    private var _binding: ActivityCameraBinding? = null
    private val binding get() = _binding!!

    // Selector showing which camera is selected (front or back)
    private var lensFacing = CameraSelector.DEFAULT_BACK_CAMERA
    private var hdrCameraSelector: CameraSelector? = null

    // Selector showing which flash mode is selected (on, off or auto)
    private var flashMode by Delegates.observable(ImageCapture.FLASH_MODE_OFF) { _, _, new ->
        if (_binding == null) return@observable

        binding.btnFlash.setImageResource(
            when (new) {
                ImageCapture.FLASH_MODE_ON -> R.drawable.ic_flash_on
                ImageCapture.FLASH_MODE_AUTO -> R.drawable.ic_flash_auto
                else -> R.drawable.ic_flash_off
            }
        )
    }

    // Selector showing is grid enabled or not
    private var hasGrid = false

    // Selector showing is hdr enabled or not (will work, only if device's camera supports hdr on hardware level)
    private var hasHdr = false

    // Selector showing is there any selected timer and it's value (3s or 10s)
    private var selectedTimer = CameraTimer.OFF

    /**
     * A display listener for orientation changes that do not trigger a configuration
     * change, for example if we choose to override config change in manifest or for 180-degree
     * orientation changes.
     */
    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit

        @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
        override fun onDisplayChanged(displayId: Int) {
            val rotate = binding.root.display?.rotation ?: 0
            preview?.targetRotation = rotate
            imageCapture?.targetRotation = rotate
            imageAnalyzer?.targetRotation = rotate
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        flashMode = prefs.getInt(KEY_FLASH, ImageCapture.FLASH_MODE_OFF)
        hasGrid = prefs.getBoolean(KEY_GRID, false)
        hasHdr = prefs.getBoolean(KEY_HDR, false)

        displayManager.registerDisplayListener(displayListener, null)

        binding.run {
            viewFinder.addOnAttachStateChangeListener(object :
                View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View) =
                    displayManager.registerDisplayListener(displayListener, null)

                override fun onViewAttachedToWindow(v: View) =
                    displayManager.unregisterDisplayListener(displayListener)
            })

            btnTakePicture.setOnClickListener { takePicture() }
            btnSwitchCamera.setOnClickListener { toggleCamera() }
            btnTimer.setOnClickListener { selectTimer() }
            btnGrid.setOnClickListener { toggleGrid() }
            btnFlash.setOnClickListener { selectFlash() }
            btnHdr.setOnClickListener { toggleHdr() }
            btnTimerOff.setOnClickListener { closeTimerAndSelect(CameraTimer.OFF) }
            btnTimer3.setOnClickListener { closeTimerAndSelect(CameraTimer.S3) }
            btnTimer10.setOnClickListener { closeTimerAndSelect(CameraTimer.S10) }
            btnFlashOff.setOnClickListener { closeFlashAndSelect(ImageCapture.FLASH_MODE_OFF) }
            btnFlashOn.setOnClickListener { closeFlashAndSelect(ImageCapture.FLASH_MODE_ON) }
            btnFlashAuto.setOnClickListener { closeFlashAndSelect(ImageCapture.FLASH_MODE_AUTO) }
            btnExposure.setOnClickListener { flExposure.visibility = View.VISIBLE }
            flExposure.setOnClickListener { flExposure.visibility = View.GONE }

            startCamera()
        }
    }

    override fun onResume() {
        setFullScreen()
        super.onResume()
    }

    /**
     * Create some initial states
     * */
    private fun initViews() {
        _binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGrid.setImageResource(if (hasGrid) R.drawable.ic_grid_on else R.drawable.ic_grid_off)
        binding.groupGridLines.visibility = if (hasGrid) View.VISIBLE else View.GONE

        binding.btnTimer.onWindowInsets { view, windowInsets ->
            val topInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            if (topInsets == 0) {
                view.topMargin = 28.px()
            }
        }
    }

    private fun Int.px(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    /**
     * Change the facing of camera
     *  toggleButton() function is an Extension function made to animate button rotation
     * */
    @SuppressLint("RestrictedApi")
    fun toggleCamera() = binding.btnSwitchCamera.toggleButton(
        flag = lensFacing == CameraSelector.DEFAULT_BACK_CAMERA,
        rotationAngle = 180f,
        firstIcon = R.drawable.ic_outline_camera_rear,
        secondIcon = R.drawable.ic_outline_camera_front,
    ) {
        lensFacing = if (it) {
            CameraSelector.DEFAULT_BACK_CAMERA
        } else {
            CameraSelector.DEFAULT_FRONT_CAMERA
        }

        startCamera()
    }

    private val outputDirectory: String by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "${Environment.DIRECTORY_DCIM}/ContainerTracker/"
        } else {
            "${getExternalFilesDir(Environment.DIRECTORY_DCIM)}/ContainerTracker/"
        }
    }

    /**
     * Show timer selection menu by circular reveal animation.
     *  circularReveal() function is an Extension function which is adding the circular reveal
     * */
    private fun selectTimer() = binding.llTimerOptions.circularReveal(binding.btnTimer)

    /**
     * This function is called from XML view via Data Binding to select a timer
     *  possible values are OFF, S3 or S10
     *  circularClose() function is an Extension function which is adding circular close
     * */
    private fun closeTimerAndSelect(timer: CameraTimer) = binding.run {
        llTimerOptions.circularClose(btnTimer) {
            selectedTimer = timer
            btnTimer.setImageResource(
                when (timer) {
                    CameraTimer.S3 -> R.drawable.ic_timer_3
                    CameraTimer.S10 -> R.drawable.ic_timer_10
                    CameraTimer.OFF -> R.drawable.ic_timer_off
                }
            )
        }
    }

    /**
     * Show flashlight selection menu by circular reveal animation.
     *  circularReveal() function is an Extension function which is adding the circular reveal
     * */
    private fun selectFlash() = binding.run {
        llFlashOptions.circularReveal(btnFlash)
    }

    /**
     * This function is called from XML view via Data Binding to select a FlashMode
     *  possible values are ON, OFF or AUTO
     *  circularClose() function is an Extension function which is adding circular close
     * */
    private fun closeFlashAndSelect(@ImageCapture.FlashMode flash: Int) = binding.run {
        llFlashOptions.circularClose(btnFlash) {
            flashMode = flash
            btnFlash.setImageResource(
                when (flash) {
                    ImageCapture.FLASH_MODE_ON -> R.drawable.ic_flash_on
                    ImageCapture.FLASH_MODE_OFF -> R.drawable.ic_flash_off
                    else -> R.drawable.ic_flash_auto
                }
            )
            imageCapture?.flashMode = flashMode
            prefs.putInt(KEY_FLASH, flashMode)
        }
    }

    /**
     * Turns on or off the grid on the screen
     * */
    private fun toggleGrid() {
        binding.btnGrid.toggleButton(
            flag = hasGrid,
            rotationAngle = 180f,
            firstIcon = R.drawable.ic_grid_off,
            secondIcon = R.drawable.ic_grid_on,
        ) { flag ->
            hasGrid = flag
            prefs.putBoolean(KEY_GRID, flag)
            binding.groupGridLines.visibility = if (flag) View.VISIBLE else View.GONE
        }
    }

    /**
     * Turns on or off the HDR if available
     * */
    private fun toggleHdr() {
        binding.btnHdr.toggleButton(
            flag = hasHdr,
            rotationAngle = 360f,
            firstIcon = R.drawable.ic_hdr_off,
            secondIcon = R.drawable.ic_hdr_on,
        ) { flag ->
            hasHdr = flag
            prefs.putBoolean(KEY_HDR, flag)
            startCamera()
        }
    }

    /**
     * Unbinds all the lifecycles from CameraX, then creates new with new parameters
     * */
    private fun startCamera() {
        // This is the CameraX PreviewView where the camera will be rendered
        val viewFinder = binding.viewFinder
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
            } catch (e: InterruptedException) {
                Toast.makeText(this, "Error starting camera", Toast.LENGTH_SHORT).show()
                return@addListener
            } catch (e: ExecutionException) {
                Toast.makeText(this, "Error starting camera", Toast.LENGTH_SHORT).show()
                return@addListener
            }

            // The display information
            val metrics = DisplayMetrics().also {
                runCatching { viewFinder.display.getRealMetrics(it) }
            }
            // The ratio for the output image and preview
            val aspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
            // The display rotation
            val rotation = try { viewFinder.display.rotation } catch (_: Throwable) { 0 }

            val localCameraProvider = cameraProvider
                ?: throw IllegalStateException("Camera initialization failed.")

            // The Configuration of camera preview
            preview = Preview.Builder()
                .setTargetAspectRatio(aspectRatio) // set the camera aspect ratio
                .setTargetRotation(rotation) // set the camera rotation
                .build()

            // The Configuration of image capture
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY) // setting to have pictures with highest quality possible (may be slow)
                .setFlashMode(flashMode) // set capture flash
                .setTargetAspectRatio(aspectRatio) // set the capture aspect ratio
                .setTargetRotation(rotation) // set the capture rotation
                .build()

            checkForHdrExtensionAvailability()

            // The Configuration of image analyzing
            imageAnalyzer = ImageAnalysis.Builder()
                .setTargetAspectRatio(aspectRatio) // set the analyzer aspect ratio
                .setTargetRotation(rotation) // set the analyzer rotation
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // in our analysis, we care about the latest image
                .build()
                .also { setLuminosityAnalyzer(it) }

            // Unbind the use-cases before rebinding them
            localCameraProvider.unbindAll()
            // Bind all use cases to the camera with lifecycle
            bindToLifecycle(localCameraProvider, viewFinder)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun checkForHdrExtensionAvailability() {
        // Create a Vendor Extension for HDR
        val extensionsManagerFuture = ExtensionsManager.getInstanceAsync(
            this, cameraProvider ?: return,
        )
        extensionsManagerFuture.addListener(
            {
                val extensionsManager = extensionsManagerFuture.get() ?: return@addListener
                val isAvailable =
                    extensionsManager.isExtensionAvailable(lensFacing, ExtensionMode.HDR)

                // check for any extension availability
                println(
                    "AUTO " + extensionsManager.isExtensionAvailable(
                        lensFacing,
                        ExtensionMode.AUTO
                    )
                )
                println(
                    "HDR " + extensionsManager.isExtensionAvailable(
                        lensFacing,
                        ExtensionMode.HDR
                    )
                )
                println(
                    "FACE RETOUCH " + extensionsManager.isExtensionAvailable(
                        lensFacing,
                        ExtensionMode.FACE_RETOUCH
                    )
                )
                println(
                    "BOKEH " + extensionsManager.isExtensionAvailable(
                        lensFacing,
                        ExtensionMode.BOKEH
                    )
                )
                println(
                    "NIGHT " + extensionsManager.isExtensionAvailable(
                        lensFacing,
                        ExtensionMode.NIGHT
                    )
                )
                println(
                    "NONE " + extensionsManager.isExtensionAvailable(
                        lensFacing,
                        ExtensionMode.NONE
                    )
                )

                // Check if the extension is available on the device
                if (!isAvailable) {
                    // If not, hide the HDR button
                    binding.btnHdr.visibility = View.GONE
                } else if (hasHdr) {
                    // If yes, turn on if the HDR is turned on by the user
                    binding.btnHdr.visibility = View.VISIBLE
                    hdrCameraSelector =
                        extensionsManager.getExtensionEnabledCameraSelector(
                            lensFacing,
                            ExtensionMode.HDR
                        )
                }
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    private fun setLuminosityAnalyzer(imageAnalysis: ImageAnalysis) {
        // Use a worker thread for image analysis to prevent glitches
        val analyzerThread = HandlerThread("LuminosityAnalysis").apply { start() }
        imageAnalysis.setAnalyzer(
            ThreadExecutor(Handler(analyzerThread.looper)),
            LuminosityAnalyzer()
        )
    }

    private fun bindToLifecycle(
        localCameraProvider: ProcessCameraProvider,
        viewFinder: PreviewView
    ) {
        try {
            localCameraProvider.bindToLifecycle(
                this, // current lifecycle owner
                hdrCameraSelector ?: lensFacing, // either front or back facing
                preview, // camera preview use case
                imageCapture, // image capture use case
                imageAnalyzer, // image analyzer use case
            ).run {
                // Init camera exposure control
                cameraInfo.exposureState.run {
                    val lower = exposureCompensationRange.lower
                    val upper = exposureCompensationRange.upper

                    binding.sliderExposure.run {
                        valueFrom = lower.toFloat()
                        valueTo = upper.toFloat()
                        stepSize = 1f
                        value = exposureCompensationIndex.toFloat()

                        addOnChangeListener { _, value, _ ->
                            cameraControl.setExposureCompensationIndex(value.toInt())
                        }
                    }
                }
            }

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(viewFinder.surfaceProvider)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to bind use cases", e)
        }
    }

    /**
     *  Detecting the most suitable aspect ratio for current dimensions
     *
     *  @param width - preview width
     *  @param height - preview height
     *  @return suitable aspect ratio
     */
    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    private fun takePicture() = lifecycleScope.launch(Dispatchers.Main) {
        // Show a timer based on user selection
        when (selectedTimer) {
            CameraTimer.S3 -> for (i in 3 downTo 1) {
                binding.tvCountDown.text = i.toString()
                delay(1000)
            }
            CameraTimer.S10 -> for (i in 10 downTo 1) {
                binding.tvCountDown.text = i.toString()
                delay(1000)
            }
            CameraTimer.OFF -> {}
        }
        binding.tvCountDown.text = ""
        captureImage()
    }

    private fun captureImage() {
        val localImageCapture =
            imageCapture ?: throw IllegalStateException("Camera initialization failed.")

        // Setup image capture metadata
        val metadata = ImageCapture.Metadata().apply {
            // Mirror image when using the front camera
            isReversedHorizontal = lensFacing == CameraSelector.DEFAULT_FRONT_CAMERA
        }
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        // Options fot the output image file
        val outputOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis())
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, outputDirectory)
            }

            // Create the output uri
            val contentUri =
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            ImageCapture.OutputFileOptions.Builder(contentResolver, contentUri, contentValues)
        } else {
            File(outputDirectory).mkdirs()
            val file = File(outputDirectory, "${System.currentTimeMillis()}.jpg")

            ImageCapture.OutputFileOptions.Builder(file)
        }.setMetadata(metadata).build()

        localImageCapture.takePicture(
            outputOptions, // the options needed for the final image
            mainExecutor(), // the executor, on which the task will run
            object :
                ImageCapture.OnImageSavedCallback { // the callback, about the result of capture process
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // This function is called if capture is successfully completed
                    outputFileResults.savedUri
                        ?.let { uri ->
                            setResult(RESULT_OK, Intent().apply {
                                data = uri
                            })
                            finish()
                        }
                }

                override fun onError(exception: ImageCaptureException) {
                    // This function is called if there is an errors during capture process
                    val msg = "Photo capture failed: ${exception.message}"
                    Toast.makeText(this@CameraActivity, msg, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, msg)
                    exception.printStackTrace()
                }
            }
        )
    }

    override fun onDestroy() {
        displayManager.unregisterDisplayListener(displayListener)
        super.onDestroy()
    }

    override fun onBackPressed() {
        binding.run {
            when {
                llTimerOptions.visibility == View.VISIBLE -> llTimerOptions.circularClose(
                    btnTimer
                )
                llFlashOptions.visibility == View.VISIBLE -> llFlashOptions.circularClose(
                    btnFlash
                )
                else -> finish()
            }
        }
    }

    companion object {
        private const val TAG = "CameraXDemo"

        const val KEY_FLASH = "sPrefFlashCamera"
        const val KEY_GRID = "sPrefGridCamera"
        const val KEY_HDR = "sPrefHDR"

        private const val RATIO_4_3_VALUE = 4.0 / 3.0 // aspect ratio 4x3
        private const val RATIO_16_9_VALUE = 16.0 / 9.0 // aspect ratio 16x9
    }
}