package com.example.dday

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.dday.model.Dday
import com.example.dday.utils.Storage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        val REQUEST_ADD_DDAY = 0
    }

    lateinit var ddayListAdapter: DdayListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Storage.init(applicationContext)

        val ddays: ArrayList<Dday> = ArrayList(Dday.getAll())

        val ddayListView: ListView = findViewById(R.id.ddayList)
        ddayListAdapter = DdayListAdapter(this, ddays)
        ddayListView.adapter = ddayListAdapter

        fab.setOnClickListener { view ->
            val intent = Intent(this, AddDdayActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD_DDAY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_ADD_DDAY) {
            if(resultCode == RESULT_OK) {
                if(data != null) {
                    val index = data.getIntExtra("index", 0)
                    ddayListAdapter.add(
                        Dday.get(index)
                    )
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
