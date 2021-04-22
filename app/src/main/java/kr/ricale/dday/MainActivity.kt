package kr.ricale.dday

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
import kotlinx.android.synthetic.main.activity_main.*
import kr.ricale.dday.model.Dday
import kr.ricale.dday.widget.LoadingIndicator

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
        const val REQUEST_ADD_DDAY = 0
        const val REQUEST_DDAY_DETAIL = 1
    }

    private lateinit var rvDday: RecyclerView
    private lateinit var appToolbar: Toolbar

    private lateinit var rvDdayAdapter: DdayRecyclerAdapter
    private lateinit var loadingIndicator: LoadingIndicator

    private var removable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvDday = findViewById(R.id.ddayList)
        appToolbar = findViewById(R.id.toolbar)
        loadingIndicator = LoadingIndicator(this)

        setDdayListView()

        setSupportActionBar(appToolbar)

        fab.setOnClickListener {
            val intent = Intent(this, AddDdayActivity::class.java)
            doneRemovable()
            startActivityForResult(intent,
                REQUEST_ADD_DDAY
            )
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
                    rvDdayAdapter.addItem(Dday.get(index))
                }
            }
        } else if(requestCode == REQUEST_DDAY_DETAIL) {
            if(resultCode == RESULT_OK) {
                if(data != null) {
                    val index = data.getIntExtra("edited", 0)
                    if(index != 0) {
                        rvDdayAdapter.updateItem(Dday.get(index))
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setDdayListView() {
        rvDdayAdapter = DdayRecyclerAdapter(
            ArrayList(Dday.getAll()),
            object : DdayRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(itemView: View, dday: Dday) {
                    if (!removable) {
                        goToDetailActivity(itemView, dday)
                    }
                }

                override fun onItemLongClick(itemView: View, dday: Dday) {
                    setRemovable()
                }
            }
        )

        rvDday.layoutManager = LinearLayoutManager(this)
        rvDday.adapter = rvDdayAdapter
    }

    private fun setRemovable() {
        if(removable) {
            return
        }

        removable = true
        rvDdayAdapter.setCheckableMode()

        setToolbarVisibility(View.VISIBLE)
    }

    private fun doneRemovable(doRemove: Boolean = false) {
        if(!removable) {
            return
        }

        removable = false
        if(doRemove) {
            rvDdayAdapter.removeSelectedItems()
        }
        rvDdayAdapter.finishCheckableMode()

        setToolbarVisibility(View.GONE)
    }

    private fun goToDetailActivity(view: View, selected: Dday) {
        val intent = Intent(this, DdayDetailActivity::class.java)
        intent.putExtra("index", selected.index)

        val activityOptions: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            Pair(view,
                DdayDetailActivity.VIEW_NAME_CONTAINER
            ),
            Pair(view.findViewById(R.id.ddayListItemDiff),
                DdayDetailActivity.VIEW_NAME_DIFF
            ),
            Pair(view.findViewById(R.id.ddayListItemName),
                DdayDetailActivity.VIEW_NAME_NAME
            ),
            Pair(view.findViewById(R.id.ddayListItemYear),
                DdayDetailActivity.VIEW_NAME_YEAR
            ),
            Pair(view.findViewById(R.id.ddayListItemMonth),
                DdayDetailActivity.VIEW_NAME_MONTH
            ),
            Pair(view.findViewById(R.id.ddayListItemDay),
                DdayDetailActivity.VIEW_NAME_DAY
            )
        )

        doneRemovable()
        ActivityCompat.startActivityForResult(this, intent,
            REQUEST_DDAY_DETAIL, activityOptions.toBundle())
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
