package com.example.dday

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.example.dday.model.Dday
import com.example.dday.utils.Storage
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_ADD_DDAY = 0
        const val REQUEST_DDAY_DETAIL = 1
    }

    private lateinit var ddayListView: ListView
    private lateinit var ddayListAdapter: DdayListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Storage.init(applicationContext)
        AndroidThreeTen.init(this)

        ddayListView = findViewById(R.id.ddayList)

        setDdayListView()

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

    private fun setDdayListView() {
        val ddays: ArrayList<Dday> = ArrayList(Dday.getAll())

        ddayListAdapter = DdayListAdapter(this, ddays)
        ddayListView.adapter = ddayListAdapter
        ddayListView.setOnItemClickListener { parent, view, position, id ->
            val selected = ddayListAdapter.getItem(position)
            if(selected != null) {
                goToDetailActivity(view, selected)
            }
        }
    }

    private fun goToDetailActivity(view: View, selected: Dday) {
        val intent = Intent(this, DdayDetailActivity::class.java)
        intent.putExtra("index", selected.index)

        val activityOptions: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            Pair<View, String>(view, DdayDetailActivity.VIEW_NAME_CONTAINER),
            Pair<View, String>(view.findViewById(R.id.ddayListItemDiff), DdayDetailActivity.VIEW_NAME_DIFF),
            Pair<View, String>(view.findViewById(R.id.ddayListItemName), DdayDetailActivity.VIEW_NAME_NAME),
            Pair<View, String>(view.findViewById(R.id.ddayListItemYear), DdayDetailActivity.VIEW_NAME_YEAR),
            Pair<View, String>(view.findViewById(R.id.ddayListItemMonth), DdayDetailActivity.VIEW_NAME_MONTH),
            Pair<View, String>(view.findViewById(R.id.ddayListItemDay), DdayDetailActivity.VIEW_NAME_DAY)
        )

        ActivityCompat.startActivityForResult(this, intent, REQUEST_DDAY_DETAIL, activityOptions.toBundle())
    }
}
