package ma.entraide.totem

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ma.entraide.totem.core.kiosk.KioskController
import ma.entraide.totem.ui.TotemApp

class MainActivity : ComponentActivity() {
    private lateinit var kioskController: KioskController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        kioskController = KioskController(window)
        kioskController.enterImmersiveMode()

        setContent {
            TotemApp()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) kioskController.enterImmersiveMode()
    }
}

