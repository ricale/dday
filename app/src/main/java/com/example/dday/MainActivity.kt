package com.example.dday

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dday.model.Dday
import com.example.dday.utils.Storage
import com.example.dday.widget.LoadingIndicator
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_ADD_DDAY = 0
        const val REQUEST_DDAY_DETAIL = 1
    }

    private lateinit var rvDday: RecyclerView
    private lateinit var appToolbar: Toolbar

    private lateinit var viewManager: RecyclerView.LayoutManager // FIXME: change name
    private lateinit var viewAdapter: DdayRecyclerAdapter // FIXME: change name
    private lateinit var loadingIndicator: LoadingIndicator

    private var removable = false
    private lateinit var ddays: ArrayList<Dday>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Storage.init(applicationContext)
        AndroidThreeTen.init(this)

        rvDday = findViewById(R.id.ddayList)
        appToolbar= findViewById(R.id.toolbar)
        loadingIndicator = LoadingIndicator(this)

        ddays = ArrayList(Dday.getAll().toList())

        setDdayListView()

        setSupportActionBar(appToolbar)

        fab.setOnClickListener {
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
                doneRemovable(doRemove = true)
                true
            } else {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if(removable) {
            doneRemovable()
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_ADD_DDAY) {
            if(resultCode == RESULT_OK) {
                if(data != null) {
                    val index = data.getIntExtra("index", 0)
                    // FIXME: ddays 에 넣지 말고, viewAdapter 에 넣을 것
                    ddays.add(Dday.get(index)!!)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setDdayListView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = DdayRecyclerAdapter(
            ddays,
            object: DdayRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(itemView: View, dday: Dday) {
                    if(!removable) {
                        goToDetailActivity(itemView, dday)
                    }
                }

                override fun onItemLongClick(itemView: View, dday: Dday) {
                    setRemovable()
                }
            }
        )

        rvDday.layoutManager = viewManager
        rvDday.adapter = viewAdapter
    }

    private fun setRemovable() {
        if(removable) {
            return
        }

        removable = true
        viewAdapter.setCheckableMode()

        setToolbarVisibility(View.VISIBLE)
    }

    private fun doneRemovable(doRemove: Boolean = false) {
        if(!removable) {
            return
        }

        removable = false
        if(doRemove) {
            viewAdapter.removeSelectedItems()
        }
        viewAdapter.finishCheckableMode()

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
        val params = rvDday.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(
            params.leftMargin,
            if(visibility != View.GONE) resources.getDimensionPixelSize(R.dimen.toolbar_height) else params.bottomMargin,
            params.rightMargin,
            params.bottomMargin

        )
        rvDday.layoutParams = params
    }
}
