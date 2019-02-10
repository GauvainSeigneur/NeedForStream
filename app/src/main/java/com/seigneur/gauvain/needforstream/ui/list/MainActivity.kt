package com.seigneur.gauvain.needforstream.ui.list
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seigneur.gauvain.needforstream.R
import com.seigneur.gauvain.needforstream.data.model.Car
import com.seigneur.gauvain.needforstream.ui.details.DetailsFragment
import com.seigneur.gauvain.needforstream.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity(), CarListCallback {

    private val mLayoutManager by lazy {
        LinearLayoutManager(this)
    }
    private var carList: MutableList<Car> = ArrayList()

    private val mCarListAdapter by lazy {
        CarListAdapter(this, carList, this)
    }

    private val mMainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private val appBarOffsetListener by lazy {
        AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val vTotalScrollRange = appBarLayout.totalScrollRange
            val vRatio = (vTotalScrollRange.toFloat() + verticalOffset) / vTotalScrollRange
            mCarImage.alpha =vRatio
        }
    }

    private val mGson by lazy {
        Gson()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mRvCars.layoutManager = mLayoutManager
        mRvCars.adapter = mCarListAdapter
        mRvCars.addItemDecoration(
            CarItemDecoration(
                resources.getDimension(R.dimen.padding_16).toInt(),
                resources.getDimension(R.dimen.padding_16).toInt()
            )
        )
        mAppBar.addOnOffsetChangedListener(appBarOffsetListener)
        mMainViewModel.init()
        subscribeToLiveData()
    }

    private fun subscribeToLiveData(){
        mMainViewModel.mListLiveMessage.observe(
            this,
            Observer {
                val type = object : TypeToken<List<Car>>() {}.type
                val carList = mGson.fromJson<List<Car>>(it, type)
                showCarList(carList)
            }
        )

       mMainViewModel.mFailureEvent.observe(
            this,
            Observer {
                    t ->  manageError(t)
            }
        )

        mMainViewModel.mOpenCarDetailView.observe(
            this,
            Observer { openDetails() }
        )
    }

    private fun showCarList(cars: List<Car>?) {
        mCarListAdapter.clear() //TODO - MANAGE THIS EVENT MORE PROPERLY
        carList.addAll(cars!!)
        mCarListAdapter.notifyDataSetChanged()
    }

    private fun manageError(throwable: Throwable) {
        if (carList.isNullOrEmpty())
            showSnackbar(throwable)
        else
            showSnackbar(throwable)
    }

    private fun showSnackbar(throwable: Throwable){
        Snackbar.make(mContentMain, throwable.message.toString(), Snackbar.LENGTH_LONG)
            .setAction("ok", {
            })
            .setActionTextColor(ContextCompat.getColor(this, R.color.colorSecondary ))
            .show()
    }

   override fun onStartClicked(car: Car?, position: Int) {
       mMainViewModel.startCar(car)
   }

    private fun openDetails(){
        //Clear Fragment back stack to avoid lifecycle exception on LiveData Observer
        clearBackStack()
        //Recreate Fragment
        val detailFragment=DetailsFragment()
        val transaction = supportFragmentManager.beginTransaction()
        detailFragment.show(supportFragmentManager, "details")
       // transaction.replace(R.id.fragLayout, detailFragment)
        //transaction.addToBackStack(null)
        //transaction.commit()
    }


    private fun clearBackStack() {
        val manager = supportFragmentManager
        if (manager.backStackEntryCount > 0) {
            val first = manager.getBackStackEntryAt(0)
            manager.popBackStackImmediate(first.id, supportFragmentManager.backStackEntryCount)
        }
    }

}
