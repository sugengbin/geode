import org.apache.geode.cache.CacheFactory
import org.apache.geode.cache.RegionShortcut
import org.apache.geode.distributed.ConfigurationProperties
import java.io.File
import java.util.*
import java.util.stream.IntStream

fun main(args: Array<String>) {
    val properties = Properties().apply {
    setProperty("locators", "localhost[44550]")
    setProperty("mcast-port", "0")
    setProperty("statistic-sampling-enabled", "true")
    setProperty(ConfigurationProperties.JMX_MANAGER, "false")
    setProperty(ConfigurationProperties.JMX_MANAGER_START, "false")
    setProperty(ConfigurationProperties.ENABLE_CLUSTER_CONFIGURATION, "false")
    }
    val cache = CacheFactory(properties).create()

    val diskStoreFactory = cache.createDiskStoreFactory()
    diskStoreFactory.setDiskDirs(arrayOf(File("/tmp")))
    diskStoreFactory.create("someDiskStore")

    val regionFactory = cache.createRegionFactory<String, String>(RegionShortcut.PARTITION_REDUNDANT_OVERFLOW)
    regionFactory.setStatisticsEnabled(true)
    regionFactory.setDiskStoreName("someDiskStore")
    val region1 = regionFactory.create("Region1")

    IntStream.range(0, Int.MAX_VALUE).forEach {
        region1[it.toString()] = region1.size.toString()
        Thread.sleep(600)
        println("Processed value $it")
    }
}