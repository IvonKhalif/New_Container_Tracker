package com.example.containertracker.utils.modules

import com.example.containertracker.data.container.ContainerRepositoryImpl
import com.example.containertracker.data.containerladen.ContainerLadenRepositoryImpl
import com.example.containertracker.data.flexi.ScanFlexiRepositoryImpl
import com.example.containertracker.data.history.HistoryRepositoryImpl
import com.example.containertracker.data.isotank.ScanIsoTankRepositoryImpl
import com.example.containertracker.data.localsales.LocalSalesRepositoryImpl
import com.example.containertracker.data.location.LocationRepositoryImpl
import com.example.containertracker.data.marking.ScanMarkingRepositoryImpl
import com.example.containertracker.data.repair.ContainerRepairRepositoryImpl
import com.example.containertracker.data.report.ReportRepositoryImpl
import com.example.containertracker.data.salesorder.SalesOrderNumberRepositoryImpl
import com.example.containertracker.data.seal.ScanSealRepositoryImpl
import com.example.containertracker.data.user.UserRepositoryImpl
import com.example.containertracker.domain.container.ContainerRepository
import com.example.containertracker.domain.containerladen.ContainerLadenRepository
import com.example.containertracker.domain.flexi.ScanFlexiRepository
import com.example.containertracker.domain.history.HistoryRepository
import com.example.containertracker.domain.isotank.ScanIsoTankRepository
import com.example.containertracker.domain.localsales.LocalSalesRepository
import com.example.containertracker.domain.location.LocationRepository
import com.example.containertracker.domain.marking.ScanMarkingRepository
import com.example.containertracker.domain.repair.ContainerRepairRepository
import com.example.containertracker.domain.report.ReportRepository
import com.example.containertracker.domain.salesorder.SalesOrderNumberRepository
import com.example.containertracker.domain.seal.ScanSealRepository
import com.example.containertracker.domain.user.UserRepository
import org.koin.dsl.module

val RepositoryModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<LocationRepository> { LocationRepositoryImpl(get()) }
    single<ContainerRepository> { ContainerRepositoryImpl(get()) }
    single<HistoryRepository> { HistoryRepositoryImpl(get()) }
    single<ReportRepository> { ReportRepositoryImpl(get()) }
    single<SalesOrderNumberRepository> { SalesOrderNumberRepositoryImpl(get()) }
    single<ScanMarkingRepository> { ScanMarkingRepositoryImpl(get()) }
    single<ScanSealRepository> { ScanSealRepositoryImpl(get()) }
    single<ScanFlexiRepository> { ScanFlexiRepositoryImpl(get()) }
    single<ScanIsoTankRepository> { ScanIsoTankRepositoryImpl(get()) }
    single<LocalSalesRepository> { LocalSalesRepositoryImpl(get()) }
    single<ContainerLadenRepository> { ContainerLadenRepositoryImpl(get()) }
    single<ContainerRepairRepository> { ContainerRepairRepositoryImpl(get()) }
}