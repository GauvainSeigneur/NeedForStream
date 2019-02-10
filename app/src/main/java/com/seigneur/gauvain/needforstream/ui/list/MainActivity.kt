package com.seigneur.gauvain.needforstream.ui.list
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.seigneur.gauvain.needforstream.R
import com.seigneur.gauvain.needforstream.data.model.Car
import com.seigneur.gauvain.needforstream.ui.details.DetailsFragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewModelProviders.of(this).get(MainViewModel::class.java)
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
        start.setOnClickListener {
            val transaction = getSupportFragmentManager().beginTransaction()
            transaction.replace(R.id.fragLayout, DetailsFragment())
            transaction.addToBackStack(null)
            transaction.commit()

        }
    }

    private fun subscribeToLiveData(){
        mMainViewModel.mLiveCars.observe(
            this,
            Observer {
                    cars -> showCarList(cars)
            }
        )

       /* mMainViewModel.mFailureEvent.observe(
            this,
            Observer {
                    t ->  manageError(t)
            }
        )*/

        mMainViewModel.mOpenCarDetailView.observe(
            this,
            Observer {
                car -> openDetails()
            }
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
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragLayout, DetailsFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }


}
