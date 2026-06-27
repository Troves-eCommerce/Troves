import android.app.Application

class TrovesApp: Application() {
    companion object{
        lateinit var application: Application
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }
}