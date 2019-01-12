package rmk.virtusa.com.quizmaster.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_select_user.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import rmk.virtusa.com.quizmaster.R
import rmk.virtusa.com.quizmaster.adapter.UsersListAdapter
import rmk.virtusa.com.quizmaster.handler.UserHandler
import rmk.virtusa.com.quizmaster.model.User
import kotlin.concurrent.thread

private const val ARG_BRANCH_ID = "bid"

class SelectUserFragment : BaseFragment() {
    private var bid: String? = null
    private var selectListener: OnUserSelectListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bid = it.getString(ARG_BRANCH_ID)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUser(user: User) {
        if (user.id.isEmpty()) return
        UserHandler.getInstance().getUsers(user.branchId).addOnSuccessListener {
            val users = it.toObjects(User::class.java)
            for (usr in users) {
                if (usr.id == UserHandler.getUserId()) {
                    users.remove(usr)
                }
            }
            usersListRecyclerView.adapter = UsersListAdapter(context, users)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_select_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usersListRecyclerView.setVisibility(View.VISIBLE)
        usersListRecyclerView.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnUserSelectListener) {
            selectListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnUserSelectListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        selectListener = null
    }

    interface OnUserSelectListener {
        fun onUserSelect(users: List<User>)
    }

    companion object {
        @JvmStatic
        fun newInstance(bid: String) =
                SelectUserFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_BRANCH_ID, bid)
                    }
                }
    }
}
