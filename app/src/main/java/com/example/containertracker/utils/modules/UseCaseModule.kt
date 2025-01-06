package com.example.containertracker.utils.modules

import com.example.containertracker.domain.container.ContainerListUseCase
import com.example.containertracker.domain.container.ContainerQRUseCase
import com.example.containertracker.domain.container.ContainerUseCase
import com.example.containertracker.domain.container.SaveHistoryUseCase
import com.example.containertracker.domain.containerladen.GetContainerLadenHistoryUseCase
import com.example.containertracker.domain.flexi.usecase.GetContainerFlexiUseCase
import com.example.containertracker.domain.flexi.usecase.SaveFlexiUseCase
import com.example.containertracker.domain.history.GetHistoryUseCase
import com.example.containertracker.domain.history.history_detail.GetHistoryDetailUseCase
import com.example.containertracker.domain.isotank.usecase.GetContainerIsoTankUseCase
import com.example.containertracker.domain.isotank.usecase.SaveIsoTankUseCase
import com.example.containertracker.domain.localsales.usecase.GetHistoryLocalSalesUseCase
import com.example.containertracker.domain.localsales.usecase.SaveFlexiLocalSalesUseCase
import com.example.containertracker.domain.localsales.usecase.SaveLocalSalesContainerUseCase
import com.example.containertracker.domain.localsales.usecase.SaveMarkingLocalSalesUseCase
import com.example.containertracker.domain.localsales.usecase.SaveSealLocalSalesUseCase
import com.example.containertracker.domain.localsales.usecase.ScanFlexiLocalSalesUseCase
import com.example.containertracker.domain.localsales.usecase.ScanLocalSalesContainerUseCase
import com.example.containertracker.domain.localsales.usecase.ScanMarkingLocalSalesUseCase
import com.example.containertracker.domain.localsales.usecase.ScanSealLocalSalesUseCase
import com.example.containertracker.domain.location.LocationsLocalSalesUseCase
import com.example.containertracker.domain.location.LocationsUseCase
import com.example.containertracker.domain.marking.usecase.GetContainerMarkingUseCase
import com.example.containertracker.domain.marking.usecase.SaveMarkingUseCase
import com.example.containertracker.domain.repair.SaveContainerRepairUseCase
import com.example.containertracker.domain.repair.ScanContainerRepairUseCase
import com.example.containertracker.domain.report.GetReportDataUseCase
import com.example.containertracker.domain.salesorder.GetSalesOrderNumberUseCase
import com.example.containertracker.domain.seal.usecase.GetContainerSealUseCase
import com.example.containertracker.domain.seal.usecase.SaveSealUseCase
import com.example.containertracker.domain.user.SignInUseCase
import org.koin.dsl.module

val UseCaseModule = module {
    single { SignInUseCase(get()) }
    single { LocationsUseCase(get()) }
    single { LocationsLocalSalesUseCase(get()) }
    single { ContainerUseCase(get(), get()) }
    single { SaveHistoryUseCase(get(), get()) }
    single { GetHistoryUseCase(get()) }
    single { GetReportDataUseCase(get()) }
    single { GetSalesOrderNumberUseCase(get()) }
    single { ContainerListUseCase(get()) }
    single { ContainerQRUseCase(get()) }
    single { GetHistoryDetailUseCase(get()) }
    single { GetContainerFlexiUseCase(get()) }
    single { SaveFlexiUseCase(get()) }
    single { GetContainerMarkingUseCase(get()) }
    single { SaveMarkingUseCase(get()) }
    single { GetContainerSealUseCase(get()) }
    single { SaveSealUseCase(get()) }
    single { GetContainerIsoTankUseCase(get()) }
    single { SaveIsoTankUseCase(get()) }
    single { ScanLocalSalesContainerUseCase(get()) }
    single { SaveLocalSalesContainerUseCase(get()) }
    single { GetHistoryLocalSalesUseCase(get()) }
    single { ScanFlexiLocalSalesUseCase(get()) }
    single { SaveFlexiLocalSalesUseCase(get()) }
    single { ScanMarkingLocalSalesUseCase(get()) }
    single { SaveMarkingLocalSalesUseCase(get()) }
    single { ScanSealLocalSalesUseCase(get()) }
    single { SaveSealLocalSalesUseCase(get()) }
    single { GetContainerLadenHistoryUseCase(get()) }
    single { ScanContainerRepairUseCase(get()) }
    single { SaveContainerRepairUseCase(get()) }
}