package com.aqua.hoophelper.home

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.HomeCardBinding
import com.aqua.hoophelper.databinding.HomeFragmentBinding

class HomeVPagerAdapter(
    private val list: List<String>,
    private val context: Context): PagerAdapter() {

    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.home_card, container, false)

        //get data

        //set data to UI
        val stat = view.findViewById<TextView>(R.id.leader_stat)
        stat.text = list[position]
        //handle card click
        val card = view.findViewById<CardView>(R.id.leader_card)
        val cardB = view.findViewById<CardView>(R.id.leader_card_b)

        var isFront = true

        // flip card
        val scale = context.resources.displayMetrics.density
        card.cameraDistance = 8000 * scale
        cardB.cameraDistance = 8000 * scale
        card.setOnClickListener {
            val animF = AnimatorInflater.loadAnimator(context, R.animator.front_animator) as AnimatorSet
            val animB = AnimatorInflater.loadAnimator(context, R.animator.back_animator) as AnimatorSet
            if (isFront) {
                animF.setTarget(card)
                animB.setTarget(cardB)
                animF.start()
                animB.start()
                isFront = false
            } else {
                animF.setTarget(cardB)
                animB.setTarget(card)
                animB.start()
                animF.start()
                isFront = true
            }
        }


        //add view to container
        container.addView(view, position)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        container.removeView(`object` as View)
    }
}