package usc.app.eventosusc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import org.jsoup.Jsoup

class MainActivity : AppCompatActivity() {

    var recyclerView : RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById( R.id.lista )

        recyclerView?.layoutManager = GridLayoutManager( this, 3 )
        recyclerView?.setHasFixedSize(true)

        doAsync {
            val html = Jsoup
                .connect("https://usc.edu.co/index.php/noticias/itemlist/category/59-banner-eventos")
                .get()

            val dt1 = html.select("h3[class=catItemTitle]").select("a")

            val dt2 = html.select("div[class=catItemIntroText]").select("img")

            val lista : ArrayList<Eventos> = ArrayList()

            val a : ArrayList<String> = ArrayList()
            val b : ArrayList<String> = ArrayList()

            for( i in dt1 )
            {
                //ConsolaDebugError("Cuenta", )  )
                a.add( i.attr("title" ) )
            }

            for( i in dt2 )
            {
                //ConsolaDebugError("Cuenta", i.attr("src")  )
                b.add( "https://usc.edu.co" + i.attr("src") )
            }

            for ( i in a.indices )
            {
                var aux = Eventos()
                aux.foto = b[ i ]
                aux.titulo = a[ i ]

                lista.add(  aux )
            }

            uiThread {
                adaptadorRV( lista )
            }
        }

    }

    private fun adaptadorRV(lista : ArrayList<Eventos> )
    {
        val adaptador = RvEvento(
            lista,
            INTERFACE_click { _: View, i: Int ->
                run {

                }
            }
        )
        recyclerView?.adapter = adaptador
        ConsolaDebugError("usc_debug", "size: " + lista.size )
    }

    fun getDatos()
    {
        val strReq = object : StringRequest(
            Method.POST,
            "http://172.16.160.66/get_recientes.php",
            Response.Listener<String> {
                try {
                    ConsolaDebugError( "Cuenta" , it )

                }catch ( e: Exception )
                {
                    e.message?.let { error ->
                        ConsolaDebugError( "Cuenta" , error )
                        ConsolaDebugError( "Cuenta" , it )
                    }
                    ConsolaDebugError("Cuenta", "try")
                }
            },
            Response.ErrorListener {
                it.message?.let { error ->
                    ConsolaDebugError("Cuenta", error)
                }
                ConsolaDebugError("Cuenta", "response")
            }
        ) {}
        MySingleton.getInstance(this).addToRequestQueue(strReq)
    }

    fun ConsolaDebugError( str : String, msg : String )
    {
        Log.d("usc_debug", "$str -> $msg");
    }

}
