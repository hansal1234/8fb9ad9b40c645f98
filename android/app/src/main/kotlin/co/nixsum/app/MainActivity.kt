package co.nixsum.app

import android.content.BroadcastReceiver
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity : FlutterActivity() {
    private val CHANNEL = "nixsum/channel"
    private val EVENTS = "nixsum/events"
    private var linksReceiver: BroadcastReceiver? = null
    private var referralCode = ""
    private var startString: String? = null

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "initialLink") {
                result.success(startString)
            } else if (call.method == "stopService") {
                result.success(true)
            }
        }

        EventChannel(flutterEngine.dartExecutor, EVENTS).setStreamHandler(
            object : EventChannel.StreamHandler {
                override fun onListen(args: Any?, events: EventSink) {
                    linksReceiver = createChangeReceiver(events)
                }

                override fun onCancel(args: Any?) {
                    linksReceiver = null
                }
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = getIntent()
        startString = intent.data?.toString()

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == Intent.ACTION_VIEW) {
            linksReceiver?.onReceive(this.applicationContext, intent)
        }
    }

    fun createChangeReceiver(events: EventSink): BroadcastReceiver? {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val dataString = intent.dataString ?:
                events.error("UNAVAILABLE", "Link unavailable", null)
                events.success(dataString)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
