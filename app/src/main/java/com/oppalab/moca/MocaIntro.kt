package com.oppalab.moca

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroCustomLayoutFragment

class MocaIntro : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setColorSkipButton(R.color.colorBlack)
        setSkipTextTypeface(R.font.bm)
        setColorDoneText(R.color.colorBlack)
        setNextArrowColor(R.color.colorBlack)

        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro_page_1))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro_page_2))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro_page_3))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro_page_4))
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        super.onSlideChanged(oldFragment, newFragment)
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }
}