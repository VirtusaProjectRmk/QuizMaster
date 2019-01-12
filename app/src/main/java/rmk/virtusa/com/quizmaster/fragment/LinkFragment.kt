package rmk.virtusa.com.quizmaster.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_link.*
import rmk.virtusa.com.quizmaster.R
import rmk.virtusa.com.quizmaster.adapter.LinkAdapter
import rmk.virtusa.com.quizmaster.handler.UserHandler
import rmk.virtusa.com.quizmaster.model.Link
import rmk.virtusa.com.quizmaster.handler.FirestoreList.OnLoadListener
import rmk.virtusa.com.quizmaster.handler.FirestoreList


private const val ARG_UID = "uid"

class LinkFragment : Fragment() {
    private var uid: String? = null
    private var links: FirestoreList<Link>? = null
    private var linkAdapter: LinkAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uid = it.getString(ARG_UID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_link, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (uid == null) return;

        profileLinkAddTextView.setOnClickListener {
            links?.add(Link("", "https://www.github.com/someone"))
            linkAdapter?.notifyDataSetChanged()
        }
        val onLoadListener = object : OnLoadListener<Link> {
            override fun onLoad() {
                linkAdapter?.notifyDataSetChanged()
            }
        }
        var isEditable = UserHandler.getUserId().equals(uid)
        links = UserHandler.getInstance().getLinks(uid, onLoadListener)
        linkAdapter = LinkAdapter(context, links, isEditable)
        profileLinkRecyclerView.adapter = linkAdapter
        profileLinkRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        profileLinkAddTextView.visibility = if (isEditable) View.VISIBLE else View.GONE
    }

    companion object {
        @JvmStatic
        fun newInstance(uid: String) =
                LinkFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_UID, uid)
                    }
                }
    }

}
