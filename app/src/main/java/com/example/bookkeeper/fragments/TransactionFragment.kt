package com.example.bookkeeper.fragments

import android.R
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookkeeper.MyApplication
import com.example.bookkeeper.adapter.TransactionAdapter
import com.example.bookkeeper.databinding.FragmentTransactionBinding
import com.example.bookkeeper.entity.Entry
import com.example.bookkeeper.factory.SignUpFactory
import com.example.bookkeeper.utils.SharedPrefManager
import com.example.bookkeeper.viewmodels.SignUpViewModel
import java.util.*


class TransactionFragment(val param: Int) : Fragment(), TransactionAdapter.listListener {
    private lateinit var signUpViewModel: SignUpViewModel

    private lateinit var binding: FragmentTransactionBinding
    private lateinit var adapter: TransactionAdapter
    private lateinit var listEntry: MutableList<Entry>
    lateinit var sharedprefManager: SharedPrefManager
    lateinit var username: String
    var fromTimeInMillis: Long? = null
    var toTimeInMillis: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        signUpViewModel = ViewModelProvider(
            this,
            SignUpFactory(MyApplication.getUserRepository(context))
        )[SignUpViewModel::class.java]

        //Getting username
        sharedprefManager = SharedPrefManager(requireContext())
        username = sharedprefManager.isLoggedIn()

        signUpViewModel.getUserByUsername(username)

        if (param == 10) {
            binding = FragmentTransactionBinding.inflate(inflater, container, false)
            binding.lifecycleOwner = viewLifecycleOwner
            binding.recyclerView.hasFixedSize()
            /** fetching ten transaction from viewModel */
            signUpViewModel.getTransactions()
            signUpViewModel.tenTransactionsList.observe(viewLifecycleOwner, Observer { list ->
                adapter = TransactionAdapter(list, context, this)
                binding.recyclerView.layoutManager = LinearLayoutManager(context)
                binding.recyclerView.adapter = adapter
            })
        } else {
            binding = FragmentTransactionBinding.inflate(inflater, container, false)
            binding.lifecycleOwner = viewLifecycleOwner
            binding.recyclerView.visibility = View.GONE
            binding.dateContainer.visibility = View.VISIBLE

            /** Open Date Picker after clicking Date TV * */
            binding.tvFromDate.setOnClickListener {
                fromDateOpnCalender()
            }
            binding.tvToDate.setOnClickListener {
                toDateOpnCalender()
            }

            binding.btnSubmit.setOnClickListener {
                binding.selectedRecyclerView.hasFixedSize()

                /**  Query the RoomDB to get data from two different date and show in
                 * recyclerview
                 * */
                if (fromTimeInMillis != null || toTimeInMillis != null) {
                    signUpViewModel.getTransactionBetweenTwoDates(
                        fromTimeInMillis!!,
                        toTimeInMillis!!
                    )
                    signUpViewModel.transactionsListBetweenTwoDates.observe(
                        viewLifecycleOwner,
                        Observer { transactionsListBetweenTwoDates ->
                            if (transactionsListBetweenTwoDates != null) {
                                listEntry = transactionsListBetweenTwoDates.toMutableList()

                                binding.tvNoResult.visibility = View.GONE
                                binding.selectedRecyclerView.visibility = View.VISIBLE

                                Log.d("TAG", "onCreateView: $transactionsListBetweenTwoDates")
                                adapter =
                                    TransactionAdapter(
                                        listEntry,
                                        context,
                                        this
                                    )
                                binding.selectedRecyclerView.layoutManager =
                                    LinearLayoutManager(context)
                                binding.selectedRecyclerView.adapter = adapter
                            } else {
                                binding.tvNoResult.visibility = View.VISIBLE
                            }
                        })
                } else {
                    binding.tvNoResult.visibility = View.VISIBLE
                }
            }
        }
        signUpViewModel.loadNextList.observe(requireActivity(), Observer {
            if (it != null) {
                Log.d("TAG", "onCreateView: "+it)
                if (it.isNotEmpty()) {
                    listEntry.addAll(it.toMutableList())
//                adapter.notifyDataSetChanged()
                    //adapter.setLoadPosition(it.size-1)
                    binding.selectedRecyclerView.post(Runnable { adapter.notifyDataSetChanged() })
                }/* else {
                    adapter.setListEnd(true)
                }*/
            }
        })
        return binding.root
    }

    private fun fromDateOpnCalender() {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        Log.d("TAG", "Calender Year, month, day: $year, $month, $day")
        var setListener: OnDateSetListener? = OnDateSetListener { datePicker, y, m, d ->
            var mon = m + 1
            val dateString = "$d/$mon/$y"
            calendar.timeZone = TimeZone.getTimeZone("Asia/Calcutta")
            calendar.set(y, m, d, 0, 0)
            fromTimeInMillis = calendar.timeInMillis
            binding.tvFromDate.text = dateString

            Log.d("TAG", "fromDateOpnCalender: $dateString")
            Log.d("TAG", "fromDateOpnCalender: $fromTimeInMillis")
        }


        var datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.Theme_Holo_Dialog_MinWidth,
            setListener,
            year,
            month,
            day
        )

        datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        datePickerDialog.show()
    }

    private fun toDateOpnCalender() {
        val calendar1 = Calendar.getInstance()
        val year = calendar1[Calendar.YEAR]
        val month = calendar1[Calendar.MONTH]
        val day = calendar1[Calendar.DAY_OF_MONTH]

        var setListener1: OnDateSetListener? = OnDateSetListener { datePicker, y, m, d ->
            val mon1 = m + 1
            val dateString = "$d/$mon1/$y"
            calendar1.set(y, m, d, 0, 0)
            calendar1.timeZone = TimeZone.getTimeZone("Asia/Calcutta");
            toTimeInMillis =
                calendar1.timeInMillis + 86400000 // add one day millisecond , to get data between two dates
            binding.tvToDate.text = dateString
            Log.d("TAG", "toTimeInMillis: $dateString")
            Log.d("TAG", "toTimeInMillis: $toTimeInMillis")
        }

        var datePickerDialog1 = DatePickerDialog(
            requireContext(),
            R.style.Theme_Holo_Dialog_MinWidth,
            setListener1,
            year,
            month,
            day
        )

        datePickerDialog1.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        datePickerDialog1.show()
    }

    override fun loadNext(id: Int) {
        //fetch next 20 results
        if (fromTimeInMillis != null || toTimeInMillis != null) {
            signUpViewModel.loadNext(id, fromTimeInMillis!!, toTimeInMillis!!)
        }

    }

}