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
    val diskStore1Name = "${System.identityHashCode(cache)}_someDiskStore"
    diskStoreFactory.create(diskStore1Name)

    val regionFactory = cache.createRegionFactory<String, String>(RegionShortcut.PARTITION_REDUNDANT_OVERFLOW)
    regionFactory.setStatisticsEnabled(true)
    regionFactory.setDiskStoreName(diskStore1Name)
    val region1 = regionFactory.create("Region1")

    val diskStoreFactory2 = cache.createDiskStoreFactory()
    diskStoreFactory2.setDiskDirs(arrayOf(File("/tmp")))
    val diskStore2Name = "${System.identityHashCode(cache)}_someDiskStore2"
    diskStoreFactory2.create(diskStore2Name)

    val regionFactory2 = cache.createRegionFactory<String, String>(RegionShortcut.PARTITION_REDUNDANT_OVERFLOW)
    regionFactory2.setStatisticsEnabled(true)
    regionFactory2.setDiskStoreName(diskStore2Name)
    val region2 = regionFactory2.create("Region3")

    if (System.getProperty("populateData")?.toBoolean() == true) {
        val random = Random()
        IntStream.range(0, Int.MAX_VALUE).forEach {
            region1[it.toString()] = region1.size.toString()

            for (int in 0..random.nextInt(5)) {
                region2[region2.size.toString()] = region2.size.toString()
            }

            if (random.nextBoolean()){
                for (int in 0..random.nextInt(5)) {
                    region1[it.toString()]
                }
            }else{
                for (int in 0..random.nextInt(5)) {
                    region2[it.toString()]
                }
            }
            Thread.sleep(100)

            println("Processed value $it")
        }
    }
}