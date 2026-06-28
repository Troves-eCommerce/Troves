import android.app.Application

class TrovesApp: Application() {
    companion object{
        lateinit var trovesApplication: Application
    }

    override fun onCreate() {
        super.onCreate()
        trovesApplication = this
        com.troves.di.initKoin()
    }
}