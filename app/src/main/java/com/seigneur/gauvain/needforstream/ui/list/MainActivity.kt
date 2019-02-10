package com.seigneur.gauvain.needforstream.ui.list
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.view.ViewStub
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.seigneur.gauvain.needforstream.utils.Constants.SCRIM_VISIBLE
import kotlinx.android.synthetic.main.accident_view.*
import timber.log.Timber


class MainActivity : AppCompatActivity(), CarListCallback {

    private val mLayoutManager by lazy {
        LinearLayoutManager(this)
    }
    private var carList: MutableList<Car> = ArrayList()

    private val mCarListAdapter by lazy {
        CarListAdapter(carList, this)
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

    // init the bottom sheet behavior
    val mBottomSheetBehavior by lazy {
        BottomSheetBehavior.from(mBsLayout)
    }

    /*
    *********************************************************************************************
    * LIFECYCLE
    *********************************************************************************************/
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
        initBottomSheetBehavior()
        mMainViewModel.init()
        subscribeToLiveData()

        mScrim.setOnClickListener {
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        mRetry.setOnClickListener {
            Timber.d("mRetry called")
            mAccidentViewlayout.visibility = View.INVISIBLE
            mMainViewModel.init()
        }

    }

    /*
    *********************************************************************************************
    * LIST CALLBACK
    *********************************************************************************************/
    override fun onStartClicked(car: Car?, position: Int) {
        mMainViewModel.startCar(car)
    }

    /*
    *********************************************************************************************
    * UI METHODS
    *********************************************************************************************/
    private fun initBottomSheetBehavior(){
        mBottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState== BottomSheetBehavior.STATE_HIDDEN) {
                    //Stop car when Bottom sheet is hidden
                    mMainViewModel.stopCar()
                    //Manage scrim state in accordance
                    mMainViewModel.setScrimeState(Constants.SCRIM_GONE)
                }
            }
            override fun onSlide( bottomSheet: View, slideOffset: Float) {
                mScrim.alpha = 1+slideOffset
                Timber.d("alpha"+slideOffset+1)
            }
        })
    }

    /**
     * Subscribe to ViewModel LiveData
     */
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

       mMainViewModel.mLiveState.observe(
           this,
           Observer {
               when (it) {
                   SCRIM_VISIBLE -> mScrim.visibility = View.VISIBLE
                   else -> mScrim.visibility = View.GONE
               }
           }
       )

        mMainViewModel.mOpenCarDetailView.observe(
            this,
            Observer { openDetails() }
        )
    }

    private fun showCarList(cars: List<Car>?) {
        mCarListAdapter.clear() //not very clean..
        carList.addAll(cars!!)
        mCarListAdapter.notifyDataSetChanged()
    }

    private fun manageError(throwable: Throwable) {
        if (carList.isNullOrEmpty())
            showViewStub(throwable)
        else
            showSnackbar(throwable)
    }

    private fun showSnackbar(throwable: Throwable){
        Snackbar.make(mContentMain, throwable.message.toString(), Snackbar.LENGTH_LONG)
            .show()
    }

    private fun openDetails(){
        //Clear Fragment back stack to avoid lifecycle exception on LiveData Observer
        clearBackStack()
        //Recreate Fragment
        val detailFragment=DetailsFragment()
        val transaction = supportFragmentManager.beginTransaction()
        //detailFragment.show(supportFragmentManager, "details")
        transaction.replace(R.id.mBsLayout, detailFragment)
        transaction.addToBackStack(null)
        transaction.commit()
        //Set bottom sheet visible
        mMainViewModel.setScrimeState(Constants.SCRIM_VISIBLE)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun clearBackStack() {
        val manager = supportFragmentManager
        if (manager.backStackEntryCount > 0) {
            val first = manager.getBackStackEntryAt(0)
            manager.popBackStackImmediate(first.id, supportFragmentManager.backStackEntryCount)
        }
    }

    private fun showViewStub(throwable: Throwable){
        Timber.d("showViewStub called")
        mAccidentViewlayout.visibility = View.VISIBLE
        mIssueText.text = throwable.message


    }

}
