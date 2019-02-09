package com.seigneur.gauvain.needforstream.ui
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.seigneur.gauvain.needforstream.R
import com.seigneur.gauvain.needforstream.data.model.Car
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
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
        MainViewModel()
    }

    private val appBarOffsetListener by lazy {
        AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val vTotalScrollRange = appBarLayout.totalScrollRange
            val vRatio = (vTotalScrollRange.toFloat() + verticalOffset) / vTotalScrollRange
            mCarImage.alpha =vRatio
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mRvCars.layoutManager = mLayoutManager
        mRvCars.adapter = mCarListAdapter
        mAppBar.addOnOffsetChangedListener(appBarOffsetListener)
        mMainViewModel.init()
        subscribeToLiveData()
    }

    private fun subscribeToLiveData(){
        mMainViewModel.mLiveCars.observe(
            this,
            Observer {
                    cars ->
                showCarList(cars)
                Timber.d("cars data changed: $cars")
            }
        )
    }

    private fun showCarList(cars: List<Car>?) {
        mCarListAdapter.clear() //TODO - MANAGE THIS EVENT MORE PROPERLY
        carList.addAll(cars!!)
        mCarListAdapter.notifyDataSetChanged()
    }

   override fun onShotDraftClicked(car: Car?, position: Int) {
       mMainViewModel.startCar(car)
   }


}
