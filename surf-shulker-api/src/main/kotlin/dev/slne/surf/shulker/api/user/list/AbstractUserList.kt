package dev.slne.surf.shulker.api.user.list

import dev.slne.surf.shulker.api.user.OfflineUser
import dev.slne.surf.shulker.api.user.online.User
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf

typealias OfflineUserList = AbstractUserList<OfflineUser>
typealias UserList = AbstractUserList<User>

class AbstractUserList<P : OfflineUser> : MutableCollection<P>, Iterable<P>, Cloneable {
    private val delegate = mutableObjectListOf<P>()

    override fun add(element: P): Boolean {
        return delegate.add(element)
    }

    override fun addAll(elements: Collection<P>): Boolean {
        return delegate.addAll(elements)
    }

    override fun clear() {
        delegate.clear()
    }

    override fun iterator(): MutableIterator<P> {
        return delegate.iterator()
    }

    override fun remove(element: P): Boolean {
        return delegate.remove(element)
    }

    override fun removeAll(elements: Collection<P>): Boolean {
        return delegate.removeAll(elements)
    }

    override fun retainAll(elements: Collection<P>): Boolean {
        return delegate.retainAll(elements)
    }

    override val size: Int
        get() = delegate.size

    override fun isEmpty(): Boolean {
        return delegate.isEmpty()
    }

    override fun contains(element: P): Boolean {
        return delegate.contains(element)
    }

    override fun containsAll(elements: Collection<P>): Boolean {
        return delegate.containsAll(elements)
    }

    override fun clone(): Any {
        val cloned = AbstractUserList<P>()
        cloned.addAll(this.delegate)
        return cloned
    }
}