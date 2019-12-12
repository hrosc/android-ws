package ep.ws

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val API_KEY = "749f1131"
        const val WS_URL = "https://www.omdbapi.com/?s=%s&apikey=$API_KEY"
        val TAG: String = MainActivity::class.java.canonicalName!!
    }

    var task: LookUp? = null

    override fun onStop() {
        task?.cancel(true)
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        search_btn.setOnClickListener {
            Log.i(TAG, "Searching ...")
            task = LookUp(this)
            task?.execute(query.text.toString())
            query.setText("")
        }
    }

    class LookUp(val activity: MainActivity) : AsyncTask<String, Unit, JSONObject>() {
        override fun doInBackground(vararg params: String?): JSONObject {
            try {
                val url = URL(String.format(WS_URL, params[0]))
                //val conn = url.openConnection() as HttpURLConnection // as -> casting

                //conn.doInput = true // poslali bomo podatke
                //conn.requestMethod = "GET"
                //conn.setRequestProperty("accept", "application/json")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    doInput = true
                    requestMethod = "GET"
                    setRequestProperty("accept", "application/json")
                }
                val scanner = Scanner(conn.inputStream).useDelimiter("\\A") // preberi vse
                return if (scanner.hasNext()) { //fancy if
                    JSONObject(scanner.next())
                } else {
                    JSONObject()
                }
            } catch (e: Exception) {
                Log.w(TAG, "Exception: ${e.localizedMessage}")
                return JSONObject()
            }
        }

        override fun onPostExecute(result: JSONObject) {
            activity.results.text = result.toString(2)
            //Log.i(TAG, "Rezultat: ${result!!.toString(2)}")
        }

    }
}
