package com.cyan.fireminigame.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.cyan.fireminigame.R
import com.cyan.fireminigame.databinding.ActivityMainBinding
import com.cyan.fireminigame.view.fragment.ChatFragment
import com.cyan.fireminigame.view.fragment.LobbyFragment
import com.cyan.fireminigame.viewmodel.ConnectionCheck
import com.cyan.fireminigame.viewmodel.ViewModelFactory
import com.cyan.fireminigame.viewmodel.activity.MainViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.drawer_main_header.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var chatFragment: ChatFragment
    private lateinit var lobbyFragment: LobbyFragment
    private lateinit var mainVM: MainViewModel
    private lateinit var vmFactory: ViewModelFactory.WithActivity
    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var connectionCheck: ConnectionCheck
    private var backKeyPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionCheck = ConnectionCheck(this)

        vmFactory = ViewModelFactory.WithActivity(this)
        mainVM = ViewModelProvider(this, vmFactory).get(MainViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.main = mainVM

        toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        toolbar.showOverflowMenu()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        drawerLayout = findViewById(R.id.drw_main)
        navigationView = findViewById(R.id.nav_main)
        navigationView.setNavigationItemSelectedListener(this)

        connectionCheck.observe(this) {
            when (it) {
                false -> connectionLostDialog()
            }
        }

        // 로그인 상태가 아닐 경우 로그인 창으로 보냄
        mainVM.siStatus.observe(this) {
            if (!it) {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        // 서버에서 닉네임을 받아오기 위해 이곳에서 Fragment 를 초기화하였음
        // 첫 화면 Fragment 는 gameFragment
        mainVM.nick.observe(this) {
            txt_dm.text = it
            val bundle = Bundle()
            bundle.putString("nick", it)
            chatFragment = ChatFragment()
            chatFragment.arguments = bundle
            lobbyFragment = LobbyFragment()
            lobbyFragment.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_main, lobbyFragment).commit()
        }

    }

    // 메뉴 불러오기
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // 메뉴 클릭시 이벤트
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
            R.id.menu_chat -> {
                if (supportFragmentManager.fragments.toString().contains("LobbyFragment")) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_main, chatFragment).commit()
                    item.setIcon(R.drawable.ic_game_24px)
                } else if (supportFragmentManager.fragments.toString().contains("ChatFragment")) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_main, lobbyFragment).commit()
                    item.setIcon(R.drawable.ic_chat_white_24dp)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 네비게이션 드로워창 아이템 선택 이벤트
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itm_logout -> {
                logoutDialog()
            }
        }
        return false
    }

    private fun connectionLostDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setTitle(resources.getString(R.string.connectionLost))
        builder.setMessage(resources.getString(R.string.connectionLostDialog))
        builder.setCancelable(false)
        builder.setPositiveButton(resources.getString(R.string.yes)) { _, _ -> }
        builder.setOnDismissListener {
            finish()
        }
        builder.show()
    }

    // 로그아웃 다이얼로그
    private fun logoutDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setTitle(resources.getString(R.string.signOut))
        builder.setMessage(resources.getString(R.string.signOutDialog))
        builder.setNegativeButton(resources.getString(R.string.no)) { _, _ -> }
            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                mainVM.signOut()
            }
            .show()

    }

    override fun onBackPressed() {
        val toast = Toast.makeText(this, resources.getString(R.string.onBackPressed),
            Toast.LENGTH_SHORT)
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            toast.show()
        } else {
            toast.cancel()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

}


