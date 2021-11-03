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
import androidx.viewpager.widget.PagerAdapter
import com.aqua.hoophelper.R
import com.aqua.hoophelper.database.PlayerStat
import com.aqua.hoophelper.match.DataType
import com.aqua.hoophelper.match.DetailDataType

class HomeVPagerAdapter(
    private val list: List<PlayerStat>,
    private val context: Context,
    private val viewModel: HomeViewModel): PagerAdapter() {

    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val leaderTypes = listOf<String>("Score", "Rebound", "Assist", "Steal", "Block")
        val view = LayoutInflater.from(context).inflate(R.layout.home_card, container, false)
        Log.d("redo2","Hi ${list}")

        Log.d("redo3","Hi ${position}")

        //get data

        //set data to UI
        val leaderName = view.findViewById<TextView>(R.id.leader_name)
        val type = view.findViewById<TextView>(R.id.leader_stat_type)
        val statMain = view.findViewById<TextView>(R.id.leader_stat_main)
        val leaderNameB = view.findViewById<TextView>(R.id.leader_name_b)
        val detailPts = view.findViewById<TextView>(R.id.leader_pts)
        val detailReb = view.findViewById<TextView>(R.id.leader_reb)
        val detailAst = view.findViewById<TextView>(R.id.leader_ast)
        val detailStl = view.findViewById<TextView>(R.id.leader_stl)
        val detailBlk = view.findViewById<TextView>(R.id.leader_blk)

        type.text = leaderTypes[position]

        val mainDataType = when(position) {
            0 -> {
                DataType.SCORE
            }
            1 -> {
                DataType.REBOUND
            }
            2 -> {
                DataType.ASSIST
            }
            3 -> {
                DataType.STEAL
            }
            4 -> {
                DataType.BLOCK
            }
            else -> DataType.TURNOVER
        }
        leaderName.text = viewModel.getLeaderMainData(mainDataType).first // TODO
        leaderNameB.text = viewModel.getLeaderMainData(mainDataType).first
        statMain.text = viewModel.getLeaderMainData(mainDataType).second

        detailPts.text = viewModel.getLeaderDetailData(DetailDataType.PTS, mainDataType)
        detailReb.text = viewModel.getLeaderDetailData(DetailDataType.REB, mainDataType)
        detailAst.text = viewModel.getLeaderDetailData(DetailDataType.AST, mainDataType)
        detailStl.text = viewModel.getLeaderDetailData(DetailDataType.STL, mainDataType)
        detailBlk.text = viewModel.getLeaderDetailData(DetailDataType.BLK, mainDataType)

        //handle card click

        // flip card
        val card = view.findViewById<CardView>(R.id.leader_card)
        val cardB = view.findViewById<CardView>(R.id.leader_card_b)

        var isFront = true

        val scale = context.resources.displayMetrics.density
        card.cameraDistance = 8000 * scale
        cardB.cameraDistance = 8000 * scale
        card.setOnClickListener {
            val animF = AnimatorInflater.loadAnimator(context, R.animator.front_animator) as AnimatorSet
            val animB = AnimatorInflater.loadAnimator(context, R.animator.back_animator) as AnimatorSet
            isFront = if (isFront) {
                animF.setTarget(card)
                animB.setTarget(cardB)
                animF.start()
                animB.start()
                false
            } else {
                animF.setTarget(cardB)
                animB.setTarget(card)
                animB.start()
                animF.start()
                true
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