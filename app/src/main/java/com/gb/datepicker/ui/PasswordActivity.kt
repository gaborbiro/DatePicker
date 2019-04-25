package com.gb.datepicker.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.doOnLayout
import com.gb.datepicker.UserPreferences
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item_year.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
import org.jetbrains.anko.editText
import org.jetbrains.anko.verticalLayout
import javax.inject.Inject
import android.text.InputType
import com.gb.datepicker.R


class PasswordActivity : DaggerAppCompatActivity() {

    @Inject
    internal lateinit var userPreferences: UserPreferences

    private lateinit var passwordNames: List<String>
    private lateinit var currentMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        passwordNames = userPreferences.passwords.keys.toList()

        userPreferences.currentPasswordName?.let {
            initAdapter()
            Toast.makeText(this, "Current password is $it", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun initAdapter() {
        view_pager.adapter = ViewPagerAdapter(
            { data, page ->
                updateSelectedDate(data)
                if (page < 2) {
                    view_pager.postDelayed({
                        view_pager.currentItem = page + 1
                    }, 100)
                } else {
                    view_pager.postDelayed({
                        view_pager.currentItem = 0
                    }, 100)
                }
            },
            getPasswordAdapter(),
            getPasswordAdapter(),
            getPasswordAdapter()
        )
    }

    private fun getPasswordAdapter() = PageAdapter(
        R.layout.content_years,
        ItemAdapter(
            itemLayoutId = R.layout.list_item_year,
            data = { _: List<String?> ->
                listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "PL", "L")
            },
            selectable = true
        ) { _, item -> year_value.text = item }
    )

    private fun updateSelectedDate(data: List<String?>) {
        val text1 = StringBuilder()
        val text2 = StringBuilder()
        val currentPassword = getCurrentPassword()
        if (currentPassword?.isNotEmpty() == true) {
            for (index in data) {
                index?.let {
                    when (it) {
                        "PL" -> {
                            text1.append("PL ")
                            text2.append(currentPassword.substring(13, 14) + " ")
                        }
                        "L" -> {
                            text1.append("L ")
                            text2.append(currentPassword.substring(14, 15) + " ")
                        }
                        else -> {
                            val i = it.toInt()
                            text1.append("$i ")
                            text2.append(currentPassword.substring(i - 1, i) + " ")
                        }
                    }
                }
            }
        }
        result_label.text = "$text1= $text2"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        currentMenu = menu
        menu.clear()
        menuInflater.inflate(R.menu.main_menu, menu)
        menu.add(1, 0, Menu.NONE, "Add password")
        passwordNames.forEachIndexed { index, name ->
            menu.add(2, index, Menu.NONE, name)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.groupId) {
            0 -> {
                when (item.itemId) {
                    R.id.action_date_picker -> {
                        startActivity(Intent(this, DatePickerActivity::class.java))
                        return true
                    }
                }
            }
            1 -> {
                if (item.itemId == 0) {
                    showNewPasswordDialog()
                }
            }
            2 -> {
                val name = passwordNames[item.itemId]
                showEditPasswordDialog(name)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getCurrentPassword() = userPreferences.passwords[userPreferences.currentPasswordName]

    private fun showNewPasswordDialog() {
        alert {
            lateinit var nameInput: EditText
            lateinit var passwordInput: EditText

            title = "Add new entry"
            customView {
                verticalLayout {
                    nameInput = editText {
                        hint = "name"
                        doOnLayout {
                            layoutParams = it.withPadding()
                        }
                        requestFocus()
                    }
                    passwordInput = editText {
                        hint = "password"
                        doOnLayout {
                            layoutParams = it.withPadding()
                        }
                    }
                }
            }
            positiveButton(android.R.string.ok) { dialog ->
                dialog.dismiss()
                val name = nameInput.text.toString()
                val password = passwordInput.text.toString()
                if (!name.isBlank() && !password.isBlank()) {
                    userPreferences.passwords[name] = password
                    userPreferences.currentPasswordName = name
                    onCreateOptionsMenu(currentMenu)
                    initAdapter()
                } else {
                    Toast.makeText(
                        this@PasswordActivity,
                        "Please specify both name and password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            negativeButton(android.R.string.cancel) { dialog ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun showEditPasswordDialog(name: String) {
        val password = userPreferences.passwords[name]
        alert {
            lateinit var nameInput: EditText
            lateinit var passwordInput: EditText

            title = "Edit entry"
            customView {
                verticalLayout {
                    nameInput = editText {
                        setText(name)
                        doOnLayout {
                            layoutParams = it.withPadding()
                        }
                        requestFocus()
                    }
                    passwordInput = editText {
                        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        setText(password)
                        doOnLayout {
                            layoutParams = it.withPadding()
                        }
                    }
                }
            }
            positiveButton(android.R.string.ok) { dialog ->
                dialog.dismiss()
                val newName = nameInput.text.toString()
                val newPassword = passwordInput.text.toString()

                if (name != newName) {
                    userPreferences.passwords.apply {
                        remove(name)
                        put(newName, newPassword)
                    }
                    passwordNames = userPreferences.passwords.keys.toList()
                } else if (password != newPassword) {
                    userPreferences.passwords[name] = newPassword
                }
                userPreferences.currentPasswordName = newName
                Toast.makeText(
                    this@PasswordActivity,
                    "Password selected: $newName",
                    Toast.LENGTH_SHORT
                ).show()
                onCreateOptionsMenu(currentMenu)
                initAdapter()
            }
            negativeButton("Delete") { dialog ->
                dialog.dismiss()
                userPreferences.passwords.remove(name)
                userPreferences.currentPasswordName = null
                onCreateOptionsMenu(currentMenu)
                initAdapter()
                Toast.makeText(this@PasswordActivity, "Password deleted", Toast.LENGTH_SHORT).show()
            }
            neutralPressed(android.R.string.cancel) { dialog ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun View.withPadding() = LinearLayout.LayoutParams(layoutParams).apply {
        val margin = resources.getDimensionPixelSize(R.dimen.double_padding)
        setMargins(margin, margin, margin, margin)
    }
}