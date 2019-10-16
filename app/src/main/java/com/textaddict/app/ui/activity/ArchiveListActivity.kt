package com.textaddict.app.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.textaddict.app.R
import com.textaddict.app.database.entity.Article
import com.textaddict.app.database.entity.ArticleType
import com.textaddict.app.ui.adapter.ArticleViewAdapter
import com.textaddict.app.ui.adapter.OnSwipeTouchListener
import com.textaddict.app.ui.adapter.TouchCallbackBuilder
import com.textaddict.app.ui.fragment.OnListFragmentInteractionListener
import com.textaddict.app.utils.dpToPx
import com.textaddict.app.utils.drawableToBitmap
import com.textaddict.app.viewmodel.impl.ListArticleViewModel
import kotlinx.android.synthetic.main.toolbar_article.*

class ArchiveListActivity : AppCompatActivity(), OnListFragmentInteractionListener {

    private var columnCount = 1
    private lateinit var viewModel: ListArticleViewModel
    private lateinit var adapter: ArticleViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive_list)

        val actionBar = findViewById<Toolbar>(R.id.toolbar_archive)
        toolbar_title.text = resources.getText(R.string.archived_articles)
        setSupportActionBar(actionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        viewModel = ViewModelProvider(this).get(ListArticleViewModel::class.java)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.list)
        recyclerView.layoutManager = when {
            columnCount <= 1 -> LinearLayoutManager(this)
            else -> GridLayoutManager(this, columnCount)
        }
        adapter = ArticleViewAdapter(recyclerView, this, false)
        recyclerView.adapter = adapter

        val dLeft = resources.getDrawable(R.drawable.ic_delete_white_24dp).mutate()
        val dRight = resources.getDrawable(R.drawable.ic_unarchive_white_24dp).mutate()
        val iconSizeInDp = 24f

        val iconLeft = drawableToBitmap(dLeft)
        val iconRight = drawableToBitmap(dRight)

        val touchCallback = TouchCallbackBuilder<Article>(adapter)
            .backgroundColor(resources.getColor(R.color.colorBackground))
            .iconSize(dpToPx(iconSizeInDp, this))
            .leftBackgroundColor(resources.getColor(R.color.colorLightBlue))
            .leftIcon(iconLeft)
            .leftTextSnackBar("Article deleted")
            .rightBackgroundColor(resources.getColor(R.color.colorLightBlue))
            .rightIcon(iconRight)
            .rightTextSnackBar("Article unarchived")
            .isMarginAppbar(false)
            .view(findViewById(android.R.id.content))
            .onSwipeListener(object : OnSwipeTouchListener {
                override fun onSwipeUndo(vh: RecyclerView.ViewHolder) {
                    viewModel.archiveArticle(ArticleViewAdapter.actionArticle!!.id)
                }

                override fun onSwipeRight(vh: RecyclerView.ViewHolder) {
                    viewModel.deleteArticle(ArticleViewAdapter.actionArticle!!.id)
                }

                override fun onSwipeLeft(vh: RecyclerView.ViewHolder) {
                    viewModel.unarchiveArticle(ArticleViewAdapter.actionArticle!!.id)
                }
            })
            .build()

        val helper = ItemTouchHelper(touchCallback)
        helper.attachToRecyclerView(recyclerView)
        recyclerView.itemAnimator = null
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            DividerItemDecoration.VERTICAL
        )
        recyclerView.addItemDecoration(dividerItemDecoration)
        subscribeUi(viewModel.archivedArticles)
    }

    private fun subscribeUi(articles: LiveData<List<Article>>) {
        articles.observe(this, Observer { listArticle ->
            listArticle?.let {
                adapter.setList(it)
            }
        })
    }

    override fun onListFragmentInteraction(item: ArticleType?) {
        val intent = Intent(this, ArticleActivity::class.java)
        if (item is Article) {
            intent.putExtra("DOMAIN", item.fullPath)
            intent.putExtra("ID", item.id)
            startActivity(intent)
        }
    }
}
