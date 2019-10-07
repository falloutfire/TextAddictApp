package com.textaddict.app.utils

import java.util.*

class StackListManager {

    companion object {
        fun updateStackIndex(list: List<String>, tabId: String) {
            while (list.indexOf(tabId) != 0) {
                val i = list.indexOf(tabId)
                Collections.swap(list, i, i - 1)
            }
        }

        fun updateStackToIndexFirst(stackList: List<String>, tabId: String) {
            val stackListSize = stackList.size
            var moveUp = 1

            while (stackList.indexOf(tabId) != stackListSize - 1) {
                val i = stackList.indexOf(tabId)
                Collections.swap(stackList, moveUp++, i)
            }
        }

        fun updateTabStackIndex(tabList: MutableList<String>, tabId: String) {
            if (!tabList.contains(tabId)) {
                tabList.add(tabId)
            }
            while (tabList.indexOf(tabId) != 0) {
                val i = tabList.indexOf(tabId)
                Collections.swap(tabList, i, i - 1)
            }
        }
    }
}