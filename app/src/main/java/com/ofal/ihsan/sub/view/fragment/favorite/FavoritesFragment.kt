package com.ofal.ihsan.sub.view.fragment.favorite

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.ofal.ihsan.sub.R
import com.ofal.ihsan.sub.R.string.*
import com.ofal.ihsan.sub.model.db.FavoriteParamsDB
import com.ofal.ihsan.sub.utils.SpaceItemDecoration
import com.ofal.ihsan.sub.utils.db
import com.ofal.ihsan.sub.utils.gone
import com.ofal.ihsan.sub.view.activities.detail.DetailActivity
import com.ofal.ihsan.sub.adapter.FavoritesAdapter

import org.jetbrains.anko.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class FavoritesFragment : Fragment(), AnkoComponent<Context> {

    private var favorites: MutableList<FavoriteParamsDB> = mutableListOf()
    private lateinit var listFavorite: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var adapter: FavoritesAdapter

    companion object {
        fun favoInstance(): FavoritesFragment = FavoritesFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = FavoritesAdapter(favorites) {
            ctx.startActivity<DetailActivity>(getString(item_eventdetail_id)
                    to "${it.eventId}",
                    getString(item_home_id) to "${it.homeTeamId}",
                    getString(item_away_id) to "${it.awayTeamId}")
        }

        listFavorite.adapter = adapter
        showFavorite()
        swipeRefresh.onRefresh {
            favorites.clear()
            showFavorite()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return createView(AnkoContext.Companion.create(ctx))
    }

    override fun createView(ui: AnkoContext<Context>): View = with(ui) {
        relativeLayout {
            lparams(width = matchParent, height = matchParent)

            swipeRefresh = swipeRefreshLayout {
                id = R.id.swipeRefresh
                setColorSchemeResources(R.color.colorAccent,
                        android.R.color.holo_green_light,
                        android.R.color.holo_orange_light,
                        android.R.color.holo_red_light)

                listFavorite = recyclerView {
                    lparams(width = matchParent, height = wrapContent)
                    id = R.id.rvNextEvent
                    layoutManager = LinearLayoutManager(ctx)
                    addItemDecoration(SpaceItemDecoration(8))
                }
            }
            progressBar = progressBar {
                id = R.id.pbNextEvent
            }.lparams {
                centerHorizontally()
            }
        }
    }

    private fun showFavorite() {
        context?.db?.use {
            swipeRefresh.isRefreshing = false
            progressBar.gone()
            val result = select(FavoriteParamsDB.TABLE_FAVORITE)
            val favorite = result.parseList(classParser<FavoriteParamsDB>())
            favorites.addAll(favorite)
            adapter.notifyDataSetChanged()
        }
    }
}