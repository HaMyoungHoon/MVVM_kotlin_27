package com.mvvm_kotlin_27

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.mvvm_kotlin_27.base.FBaseFunc
import com.mvvm_kotlin_27.data.FConstants
import com.mvvm_kotlin_27.data.FDelegateVoid
import com.mvvm_kotlin_27.screen.LoginFrag
import com.mvvm_kotlin_27.screen.Menu2Frag
import com.mvvm_kotlin_27.screen.recyclerfrag.RecyclerViewFrag

class MainActivityViewModel(context: Context, activity: Activity, supportFragmentManager: FragmentManager) : FDelegateVoid
{
    private var _preFrag : Fragment? = null
    private val _context : Context = context
    private val _activity : Activity = activity
    private val _supportFragmentManager : FragmentManager = supportFragmentManager
    private val _recyclerview : RecyclerViewFrag
    private val _menu2 : Menu2Frag
    private val _login : LoginFrag
    val collapseVisible : ObservableField<Int> = ObservableField<Int>(View.GONE)
    val menuVisible : ObservableField<Int> = ObservableField<Int>(View.GONE)
    val recyclerviewSelected : ObservableField<Boolean> = ObservableField<Boolean>(false)
    val menu2Selected : ObservableField<Boolean> = ObservableField<Boolean>(false)
    val logOutSelected : ObservableField<Boolean> = ObservableField<Boolean>(true)

    init
    {
        requestExternalPermissions()
        if (hasPermissionsGranted(FConstants.MANI_STORAGE_PERM))
        {
            FBaseFunc.ins.config.loadConfig()
        }
        setButtonColor(R.id.btnLogout)
        _recyclerview = RecyclerViewFrag()
        _menu2 = Menu2Frag()
        _login = LoginFrag(this)
        setContentFragment(_login)
        FBaseFunc.ins.initSql(_context)
    }

    override fun delegate()
    {
        showToast("로그인")

        collapseVisible.set(View.VISIBLE)
        menuVisible.set(View.VISIBLE)

        recyclerviewCommand()
    }
    fun collapseCommand()
    {
        if (menuVisible.get() == View.GONE)
        {
            menuVisible.set(View.VISIBLE)
        }
        else
        {
            menuVisible.set(View.GONE)
        }
    }
    fun recyclerviewCommand()
    {
        if (!hasPermissionsGranted(FConstants.MANI_STORAGE_PERM))
        {
            requestExternalPermissions()
            showToast("저장공간 권한을 허용해주세요")
            return
        }
        if (!FBaseFunc.ins.config.loaded)
        {
            FBaseFunc.ins.config.loadConfig()
        }

        setButtonColor(R.id.btnRecyclerView)
        setContentFragment(_recyclerview)
    }
    fun menu2Command()
    {
        setButtonColor(R.id.btnMenu2)
        setContentFragment(_menu2)
    }
    fun logoutCommand()
    {
        FBaseFunc.ins.logout()

        collapseVisible.set(View.GONE)
        menuVisible.set(View.GONE)

        setButtonColor(R.id.btnLogout)
        setContentFragment(_login)
    }

    private fun setContentFragment(fragment : Fragment)
    {
        if (_preFrag == fragment)
        {
            return
        }

        val trans = _supportFragmentManager.beginTransaction()
        if (_preFrag != null)
        {
            trans.remove(_preFrag!!)
//            trans.detach(_preFrag!!)
        }
        trans.add(R.id.flContent, fragment)
        _preFrag = fragment
        trans.commit()
    }
    private fun setButtonColor(id : Int)
    {
        when (id)
        {
            R.id.btnRecyclerView ->
            {
                _activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                recyclerviewSelected.set(true)
                menu2Selected.set(false)
                logOutSelected.set(false)
            }
            R.id.btnMenu2 ->
            {
                _activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                recyclerviewSelected.set(false)
                menu2Selected.set(true)
                logOutSelected.set(false)
            }
            R.id.btnLogout ->
            {
                _activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                recyclerviewSelected.set(false)
                menu2Selected.set(false)
                logOutSelected.set(true)
            }
        }
    }

    // region permissions
    private fun shouldShowRequestPermissionRationale(permissions: Array<String>) = permissions.any { ActivityCompat.shouldShowRequestPermissionRationale(_activity, it) }
    private fun hasPermissionsGranted(permissions: Array<String>) = permissions.none {
        ContextCompat.checkSelfPermission((_context), it) != PackageManager.PERMISSION_GRANTED
    }
    private fun requestExternalPermissions()
    {
        if (!shouldShowRequestPermissionRationale(FConstants.MANI_STORAGE_PERM))
        {
            requestPermissions(_activity, FConstants.MANI_STORAGE_PERM, FConstants.REQ_STORAGE_PERM)
        }
    }
    // endregion

    private fun showToast(message : String) = _activity.runOnUiThread { Toast.makeText(_context, message, Toast.LENGTH_SHORT).show() }
}