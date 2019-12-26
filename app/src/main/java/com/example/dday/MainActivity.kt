package com.example.dday

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.example.dday.model.Dday
import com.example.dday.utils.Storage
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_main.fab

class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_ADD_DDAY = 0
        const val REQUEST_DDAY_DETAIL = 1
    }

    private lateinit var ddayListView: ListView
    private lateinit var ddayListAdapter: DdayListAdapter
    private lateinit var appToolbar: Toolbar
    private var removable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Storage.init(applicationContext)
        AndroidThreeTen.init(this)

        ddayListView = findViewById(R.id.ddayList)
        appToolbar= findViewById(R.id.toolbar)

        setDdayListView()

        setSupportActionBar(appToolbar)

        fab.setOnClickListener { view ->
            val intent = Intent(this, AddDdayActivity::class.java)
            doneRemovable()
            startActivityForResult(intent, REQUEST_ADD_DDAY)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_remove -> if(removable) {
                doneRemovable()
                true
            } else {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if(removable) {
            doneRemovable(doRemove = false)
        } else {
            super.onBackPressed()
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

    private fun setDdayListView() {
        val ddays: ArrayList<Dday> = ArrayList(Dday.getAll())

        ddayListAdapter = DdayListAdapter(this, ddays)
        ddayListView.adapter = ddayListAdapter
        ddayListView.setOnItemClickListener { parent, view, position, id ->
            val selected = ddayListAdapter.getItem(position)
            if(!removable && selected != null) {
                goToDetailActivity(view, selected)
            }
        }
        ddayListView.setOnItemLongClickListener { parent, view, position, id ->
            setRemovable()
            true
        }
    }

    private fun setRemovable() {
        removable = true
        ddayListAdapter.setCheckableMode()

        setToolbarVisibility(View.VISIBLE)
    }

    private fun doneRemovable(doRemove: Boolean = true) {
        removable = false
        if(doRemove) {
            ddayListAdapter.removeSelectedItems()
        }
        ddayListAdapter.finishCheckableMode()

        setToolbarVisibility(View.GONE)
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

        doneRemovable()
        ActivityCompat.startActivityForResult(this, intent, REQUEST_DDAY_DETAIL, activityOptions.toBundle())
    }

    private fun setToolbarVisibility(visibility: Int) {
        appToolbar.visibility = visibility
        val params = ddayListView.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(
            params.leftMargin,
            if(visibility != View.GONE) resources.getDimensionPixelSize(R.dimen.toolbar_height) else params.bottomMargin,
            params.rightMargin,
            params.bottomMargin

        )
        ddayListView.layoutParams = params
    }
}
