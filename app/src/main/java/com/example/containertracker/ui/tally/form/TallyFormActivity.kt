package com.example.containertracker.ui.tally.form

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.ActivityResult
import com.example.containertracker.R
import com.example.containertracker.base.BaseActivity
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.tally.response.TallySheetDetailResponse
import com.example.containertracker.data.tally.response.TallySheetDrafts
import com.example.containertracker.databinding.ActivityTallyFormBinding
import com.example.containertracker.ui.home.selectcondition.SelectConditionBottomSheet
import com.example.containertracker.ui.tally.formpicture.TallyFormPictureBottomSheet
import com.example.containertracker.ui.tally.spm.ScanSPMActivity
import com.example.containertracker.utils.TimeUtil
import com.example.containertracker.utils.constants.ContainerConstant
import com.example.containertracker.utils.constants.ExtrasConstant.EXTRA_CONTAINER_DATA
import com.example.containertracker.utils.constants.ExtrasConstant.EXTRA_TALLY_SHEET_DATA
import com.example.containertracker.utils.constants.ResultCode
import com.example.containertracker.utils.enums.ConditionEnum
import com.example.containertracker.utils.enums.ContainerSidesEnum
import com.example.containertracker.utils.enums.ContainerTallyConditionEnum
import com.example.containertracker.utils.enums.getContainerCondition
import com.example.containertracker.utils.enums.getIcon
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class TallyFormActivity : BaseActivity() {

    private lateinit var binding: ActivityTallyFormBinding
    private val viewModel: TallyFormViewModel by viewModel()

    private val tallySheet by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_TALLY_SHEET_DATA, TallySheetDrafts::class.java)
        } else {
            intent.getParcelableExtra(EXTRA_TALLY_SHEET_DATA)
        }
    }

    private val container by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_CONTAINER_DATA, Container::class.java)
        } else {
            intent.getParcelableExtra(EXTRA_CONTAINER_DATA)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTallyFormBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        initObserver()
        initView()
    }

    private fun initObserver() {
        viewModel.apply {
            startTimeLiveData.observe(this@TallyFormActivity) {
                if (!it.isNullOrBlank())
                    binding.startTime.setText(it)
            }
            loadingState.observe(this@TallyFormActivity) { isLoading ->
                handleLoadingWidget(isLoading)
            }
            networkErrorState.observe(this@TallyFormActivity) { error ->
                showErrorMessage(error)
            }
            serverErrorState.observe(this@TallyFormActivity) { error ->
                showErrorMessage(error.status.orEmpty())
            }
            onSuccessSubmit.observeAction(this@TallyFormActivity) {
                showFormPicture()
            }
            tallyDetailLiveData.observe(this@TallyFormActivity, ::assignDataTally)
            roofConditionData.observe(this@TallyFormActivity) {
                updateSidesCondition(it, ContainerSidesEnum.ROOF)
            }
            doorConditionData.observe(this@TallyFormActivity) {
                updateSidesCondition(it, ContainerSidesEnum.DOOR)
            }
            wallConditionData.observe(this@TallyFormActivity) {
                updateSidesCondition(it, ContainerSidesEnum.WALL)
            }
            floorConditionData.observe(this@TallyFormActivity) {
                updateSidesCondition(it, ContainerSidesEnum.FLOOR)
            }
        }
    }

    private fun initView() = with(binding) {
        supportActionBar?.title = getString(R.string.tally_sheet_form_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        containerCodeValue.text = tallySheet?.codeContainer ?: "-"
        startTime.setTitle("Start Time")
        startTime.setTextForHint("Choose Time")
        startTime.onClickDropDownListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                viewModel.startTimeLiveData.value = TimeUtil.formatLocalDateToString(cal.time)
            }
            showTimePicker(cal, timeSetListener)
        }
        roofConditionTally.setTitle(ContainerConstant.ROOF)
        roofConditionTally.onClickDropDownListener {
            selectCondition(ContainerSidesEnum.ROOF)
        }

        floorConditionTally.setTitle(ContainerConstant.FLOOR)
        floorConditionTally.onClickDropDownListener {
            selectCondition(ContainerSidesEnum.FLOOR)
        }
        doorConditionTally.setTitle(ContainerConstant.DOOR)
        doorConditionTally.onClickDropDownListener {
            selectCondition(ContainerSidesEnum.DOOR)
        }

        wallConditionTally.setTitle(ContainerConstant.WALL)
        wallConditionTally.onClickDropDownListener {
            selectCondition(ContainerSidesEnum.WALL)
        }
        buttonSubmit.setOnClickListener {
            viewModel.save(
                tallySheet,
                forkliftDriver = inputForkliftDriver.getTextInput(),
                stuffer = inputStuffer.getTextInput(),
                palletQuantity = inputQuantityPallet.getTextInput(),
                bagsQuantity = inputQuantityBags.getTextInput(),
                palletId = inputPalletId.getTextInput(),
                stripingBelt = inputStripingBelt.getTextInput(),
                polyfoam = inputPolyfoam.getTextInput(),
                polywood = inputPolywood.getTextInput(),
                cartoonSheet2x1 = inputCartoonSheet237m.getTextInput(),
                cartoonSheet1x1 = inputCartoonSheet11m.getTextInput(),
                paperBag = inputPaperBag.getTextInput(),
                jumboBag = inputJumboBag.getTextInput(),
                stellDrum = inputStellDrum.getTextInput(),
                hdpeDrum = inputHdpeDrum.getTextInput(),
                ibcTank = inputIbcTank.getTextInput(),
                totalLabel = inputTotalLabel.getTextInput(),
            )
        }

        checkboxPlasticWrappedYes.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isPlasticWrappedYesChecked.value = isChecked
            if (checkboxPlasticWrappedNo.isChecked)
                checkboxPlasticWrappedNo.isChecked = !isChecked
        }
        checkboxPlasticWrappedNo.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isPlasticWrappedNoChecked.value = isChecked
            if (checkboxPlasticWrappedYes.isChecked)
                checkboxPlasticWrappedYes.isChecked = !isChecked
        }
        checkboxTali.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isTaliChecked.value = isChecked
        }
        checkboxLeftLockBar.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isLeftLockBarChecked.value = isChecked
        }
        checkboxRightLockBar.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isRightLockBarChecked.value = isChecked
        }
        checkboxDg.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isDGChecked.value = isChecked
            countingTotalLabel(isChecked)
        }
        checkboxGhs.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isGHSChecked.value = isChecked
            countingTotalLabel(isChecked)
        }
        checkboxPsn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isPSNChecked.value = isChecked
            countingTotalLabel(isChecked)
        }
        checkboxPalletized.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isPalletizedChecked.value = isChecked
        }
        checkboxUnpalletized.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isUnPalletizedChecked.value = isChecked
        }
        checkboxShipMark.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isShipMarkChecked.value = isChecked
            countingTotalLabel(isChecked)
        }
        checkboxStripingBent.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isStripingBentChecked.value = isChecked
        }
        checkboxNetralPaperBag.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isNetralPaperBagChecked.value = isChecked
        }
        checkboxSociPaperBag.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isSociPaperBagChecked.value = isChecked
        }
        checkboxScplPaperBag.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isScplPaperBagChecked.value = isChecked
        }
        checkboxBlueStellDrum.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isBlueStellDrumChecked.value = isChecked
        }
        checkboxGreenStellDrum.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isGreenStellDrumChecked.value = isChecked
        }
        checkboxGreyStellDrum.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isGreyStellDrumChecked.value = isChecked
        }

        viewModel.getDetail(tallySheet?.idTallySheet.orEmpty())
    }

    fun countingTotalLabel(isAdd: Boolean) {
        val totalLabel = binding.inputTotalLabel.getTextInput() ?: "0"
        val totalLabelInt: Int = if (totalLabel == "") 0 else totalLabel.toInt()
        binding.inputTotalLabel.setTextInput(if (isAdd) totalLabelInt.inc().toString() else totalLabelInt.dec().toString())
    }

    private fun assignDataTally(data: TallySheetDetailResponse) {
        binding.apply {
            checkboxLeftLockBar.isChecked = viewModel.isConditionChecked(data.leftLockBar)
            checkboxRightLockBar.isChecked = viewModel.isConditionChecked(data.rightLockBar)
            checkboxPlasticWrappedYes.isChecked = viewModel.isPlasticWrappedYes(data.plasticWrapped)
            checkboxPlasticWrappedNo.isChecked = viewModel.isPlasticWrappedNo(data.plasticWrapped)
            checkboxTali.isChecked = viewModel.isConditionChecked(data.tali)
            checkboxStripingBent.isChecked = viewModel.isConditionChecked(data.stripingBent)
            checkboxPalletized.isChecked = viewModel.isConditionChecked(data.palletized)
            checkboxUnpalletized.isChecked = viewModel.isConditionChecked(data.unpalletized)
            checkboxShipMark.isChecked = viewModel.isConditionChecked(data.shipMark)
            checkboxGhs.isChecked = viewModel.isConditionChecked(data.ghs)
            checkboxDg.isChecked = viewModel.isConditionChecked(data.dg)
            checkboxPsn.isChecked = viewModel.isConditionChecked(data.psn)
            checkboxNetralPaperBag.isChecked = viewModel.isConditionChecked(data.netralPaperBag)
            checkboxSociPaperBag.isChecked = viewModel.isConditionChecked(data.sociPaperBag)
            checkboxScplPaperBag.isChecked = viewModel.isConditionChecked(data.scplPaperBag)
            checkboxBlueStellDrum.isChecked = viewModel.isConditionChecked(data.blueStellDrum)
            checkboxGreyStellDrum.isChecked = viewModel.isConditionChecked(data.greyStellDrum)
            checkboxGreenStellDrum.isChecked = viewModel.isConditionChecked(data.greenStellDrum)
            sealNumberValue.text = data.sealNumber
            inputForkliftDriver.setTextInput(data.driverForklift)
            inputStuffer.setTextInput(data.stuffer)
            startTime.setText(data.startTime.orEmpty())
            inputQuantityPallet.setTextInput(data.quantityPallet)
            inputQuantityBags.setTextInput(data.quantityBags)
            inputPalletId.setTextInput(data.idPallet)
            roofConditionTally.setText(data.roofCondition.getContainerCondition().type)
            floorConditionTally.setText(data.floorCondition.getContainerCondition().type)
            wallConditionTally.setText(data.wallCondition.getContainerCondition().type)
            doorConditionTally.setText(data.doorCondition.getContainerCondition().type)
            inputPolyfoam.setTextInput(data.polyfoam)
            inputCartoonSheet11m.setTextInput(data.cartoonSheet1x1)
            inputCartoonSheet237m.setTextInput(data.cartoonSheet2x1)
            inputStripingBelt.setTextInput(data.stripingBelt)
            inputPolywood.setTextInput(data.polywood)
            inputPaperBag.setTextInput(data.paperBag)
            inputJumboBag.setTextInput(data.jumboBag)
            inputStellDrum.setTextInput(data.stellDrum)
            inputHdpeDrum.setTextInput(data.hdpeDrum)
            inputIbcTank.setTextInput(data.ibcTank)
            inputTotalLabel.setTextInput(data.totalLabel)
        }
    }

    private fun showFormPicture() {
        TallyFormPictureBottomSheet.newInstance(
            viewModel.tallyDetailLiveData.value
        ).apply {
            onSubmit = {
                goToScanSPM()
            }
        }.also {
            it.show(supportFragmentManager, "tag")
        }
    }

    private fun goToScanSPM() {
        val intent = Intent(this, ScanSPMActivity::class.java)
        intent.putExtra(EXTRA_CONTAINER_DATA, container)
        intent.putExtra(EXTRA_TALLY_SHEET_DATA, tallySheet)
        startForResult.launch(intent)
    }

    private fun Spinner.setSelectedListener(onItemSelected: (String?) -> Unit) {
        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position) as? String
                if (selectedItem == ContainerTallyConditionEnum.SELECT_CONDITION.desc)
                    onItemSelected("")
                else
                    onItemSelected(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Don't do anything
            }
        }
    }

    private fun createConditionSelection(): ArrayAdapter<String> {
        val items: List<String> = ContainerTallyConditionEnum.entries.map { it.desc }
        return ArrayAdapter(
            this@TallyFormActivity, android.R.layout.simple_spinner_item,
            items
        )
    }

    private fun showTimePicker(
        calendar: Calendar,
        timeSetListener: TimePickerDialog.OnTimeSetListener
    ) {
        TimePickerDialog(
            this@TallyFormActivity,
            timeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(result: ActivityResult) {
        super.onActivityResult(result)
        if (result.resultCode == ResultCode.RESULT_SUCCESS_SUBMIT) {
            val resultIntent = Intent()
            setResult(ResultCode.RESULT_SUCCESS_SUBMIT, resultIntent)
            finish()
        }
    }

    /**
     * show select condition bottom sheet
     */
    private fun selectCondition(type: ContainerSidesEnum) {
        val selectPostBottomSheet = SelectConditionBottomSheet(true) { condition ->
            viewModel.onSelectConditionChanged(side = type, conditionEnum = condition)
        }
        selectPostBottomSheet.show(supportFragmentManager, selectPostBottomSheet.tag)
    }

    private fun updateSidesCondition(
        conditionEnum: ConditionEnum?,
        containerSide: ContainerSidesEnum
    ) {
        when (containerSide) {
            ContainerSidesEnum.DOOR -> conditionEnum?.let {
                binding.doorConditionTally.setDrawableEnd(it.getIcon())
                binding.doorConditionTally.setText(getContainerConditionText(it))
            }

            ContainerSidesEnum.WALL -> conditionEnum?.let {
                binding.wallConditionTally.setDrawableEnd(it.getIcon())
                binding.wallConditionTally.setText(getContainerConditionText(it))
            }

            ContainerSidesEnum.ROOF -> conditionEnum?.let {
                binding.roofConditionTally.setDrawableEnd(it.getIcon())
                binding.roofConditionTally.setText(getContainerConditionText(it))
            }

            ContainerSidesEnum.FLOOR -> conditionEnum?.let {
                binding.floorConditionTally.setDrawableEnd(it.getIcon())
                binding.floorConditionTally.setText(getContainerConditionText(it))
            }
            else -> {}
        }
    }

    private fun getContainerConditionText(conditionEnum: ConditionEnum): String {
        return if (conditionEnum == ConditionEnum.GOOD)
            getString(R.string.tally_sheet_container_condition_good)
        else
            conditionEnum.type
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDetail(tallySheet?.idTallySheet.orEmpty(), isShowLoading = false)
    }
}