package net.grandcentrix.blutufintegration.ui

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragment
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import net.grandcentrix.blutufintegration.R
import org.hamcrest.Matchers.not
import androidx.fragment.app.testing.launchFragmentInContainer
import org.junit.Rule


@RunWith(AndroidJUnit4::class)
class LaunchFragmentTest {


    private fun startFragment(): FragmentScenario<LaunchFragment> = launchFragmentInContainer()

    private fun startFragment(bluetoothSupported: Boolean, permissionsGuaranteed: Boolean) {

        launchFragmentInContainer<LaunchFragment>()

        if (bluetoothSupported) {
            onView(withId(R.id.checkbox_ble)).check(matches(isChecked()))
        } else {
            onView(withId(R.id.checkbox_ble)).check(matches(isNotChecked()))
        }

        if (permissionsGuaranteed) {
            onView(withId(R.id.checkbox_permission)).check(matches(isChecked()))
            onView(withId(R.id.btn_start)).check(matches(isEnabled()))
        } else {
            onView(withId(R.id.checkbox_permission)).check(matches(isNotChecked()))
            onView(withId(R.id.btn_start)).check(matches(not(isEnabled())))
        }
    }

    @Test
    fun start_fragment_when_ble_not_supported() {
        startFragment(bluetoothSupported = false, permissionsGuaranteed = false)
    }

    @Test
    fun start_fragment_when_ble_supported() {
        startFragment(bluetoothSupported = true, permissionsGuaranteed = false)
    }

    @Test
    fun start_fragment_when_permissions_needed() {
        startFragment(bluetoothSupported = true, permissionsGuaranteed = false)
    }

   /* @Test
    fun perform_when_permissions_guaranteed() {
        startFragment()
        onView(withId(R.id.btn_start)).check(matches(isEnabled()))
        onView(withText("Start")).check(matches(isDisplayed()))
    }*/

}