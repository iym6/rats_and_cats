package ru.ilyamorozov.rats_and_cats

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.ilyamorozov.rats_and_cats.ui.activity.MainActivity

@RunWith(AndroidJUnit4::class)
class LevelsFragmentTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testLevelsDisplayed() {
        // Переходим к LevelsFragment
        onView(withId(R.id.levelsButton)).perform(click())
        onView(withId(R.id.levelsRecyclerView)).check(matches(isDisplayed()))
        // Проверяем, что первый уровень отображается
        onView(withText("Уровень 1")).check(matches(isDisplayed()))
    }
}