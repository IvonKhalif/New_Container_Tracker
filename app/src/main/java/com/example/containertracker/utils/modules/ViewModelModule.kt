package com.example.containertracker.utils.modules

import com.example.containertracker.ui.container.ContainerViewModel
import com.example.containertracker.ui.container.detail.ContainerDetailViewModel
import com.example.containertracker.ui.flexi.ScanFlexiViewModel
import com.example.containertracker.ui.flexi.form.FlexiFormViewModel
import com.example.containertracker.ui.flexi.preview.FlexiPreviewViewModel
import com.example.containertracker.ui.history.HistoryViewModel
import com.example.containertracker.ui.history.container_image_detail.HistoryContainerDetailViewModel
import com.example.containertracker.ui.history.detail.HistoryDetailViewModel
import com.example.containertracker.ui.home.HomeViewModel
import com.example.containertracker.ui.home.containercondition.ContainerConditionViewModel
import com.example.containertracker.ui.home.containerdefect.ContainerDefectFormViewModel
import com.example.containertracker.ui.home.salesordernumber.SalesOrderNumberViewModel
import com.example.containertracker.ui.isotank.ScanIsotankViewModel
import com.example.containertracker.ui.isotank.form.IsoTankFormViewModel
import com.example.containertracker.ui.localsales.ScanLocalSalesViewModel
import com.example.containertracker.ui.login.LoginViewModel
import com.example.containertracker.ui.main.MainViewModel
import com.example.containertracker.ui.marking.ScanMarkingLocalSalesViewModel
import com.example.containertracker.ui.marking.ScanMarkingViewModel
import com.example.containertracker.ui.marking.form.MarkingFormViewModel
import com.example.containertracker.ui.marking.preview.MarkingPreviewViewModel
import com.example.containertracker.ui.repair.ContainerRepairViewModel
import com.example.containertracker.ui.repair.form.ContainerRepairFormViewModel
import com.example.containertracker.ui.report.ReportViewModel
import com.example.containertracker.ui.scanner.ScannerViewModel
import com.example.containertracker.ui.seal.ScanSealLocalSalesViewModel
import com.example.containertracker.ui.seal.ScanSealViewModel
import com.example.containertracker.ui.seal.form.SealFormViewModel
import com.example.containertracker.ui.seal.preview.SealPreviewViewModel
import com.example.containertracker.ui.selectlocation.SelectLocationViewModel
import com.example.containertracker.ui.tally.ScanContainerTallyViewModel
import com.example.containertracker.ui.tally.draft.TallyContainerDraftViewModel
import com.example.containertracker.ui.tally.form.TallyFormViewModel
import com.example.containertracker.ui.tally.pallet.formpallet.PalletFormViewModel
import com.example.containertracker.ui.tally.formpicture.TallyFormPictureViewModel
import com.example.containertracker.ui.tally.pallet.ScanPalletViewModel
import com.example.containertracker.ui.tally.pallet.list.PalletViewModel
import com.example.containertracker.ui.tally.spm.ScanSPMViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ViewModelModule = module {
    viewModel { MainViewModel(get(), get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { SelectLocationViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { HistoryViewModel(get()) }
    viewModel { HistoryDetailViewModel(get(), get(), get()) }
    viewModel { ReportViewModel() }
    viewModel { ScannerViewModel(get()) }
    viewModel { ContainerConditionViewModel(get(), get()) }
    viewModel { SalesOrderNumberViewModel() }
    viewModel { ContainerViewModel(get()) }
    viewModel { ContainerDetailViewModel(get()) }
    viewModel { HistoryContainerDetailViewModel(get()) }
    viewModel { ScanMarkingViewModel(get(), get()) }
    viewModel { ScanMarkingLocalSalesViewModel(get()) }
    viewModel { MarkingFormViewModel() }
    viewModel { MarkingPreviewViewModel(get(), get()) }
    viewModel { ScanSealViewModel(get(), get()) }
    viewModel { ScanSealLocalSalesViewModel(get()) }
    viewModel { SealFormViewModel() }
    viewModel { SealPreviewViewModel(get(), get()) }
    viewModel { ScanFlexiViewModel(get(), get()) }
    viewModel { FlexiFormViewModel() }
    viewModel { FlexiPreviewViewModel(get(), get()) }
    viewModel { ScanIsotankViewModel(get()) }
    viewModel { IsoTankFormViewModel(get()) }
    viewModel { ScanLocalSalesViewModel(get(), get()) }
    viewModel { ContainerDefectFormViewModel() }
    viewModel { ContainerRepairViewModel(get(), get()) }
    viewModel { ContainerRepairFormViewModel() }
    viewModel { ScanContainerTallyViewModel(get()) }
    viewModel { TallyFormViewModel(get(), get()) }
    viewModel { TallyFormPictureViewModel(get()) }
    viewModel { ScanSPMViewModel(get()) }
    viewModel { ScanPalletViewModel(get(), get()) }
    viewModel { PalletFormViewModel(get(), get(), get()) }
    viewModel { PalletViewModel(get(),get(), get()) }
    viewModel { TallyContainerDraftViewModel(get(), get()) }
}