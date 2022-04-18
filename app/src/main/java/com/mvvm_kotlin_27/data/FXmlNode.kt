package com.example.mvvm_kotlin_27.data

open class FXmlNode : FXmlBase
{
    protected var _tabCount : Int
    protected var _startTag : String?
    protected var _endTag : String?
    protected var _text : String?
    protected var _parent : FXmlNode?
    protected var _prev : FXmlNode?
    protected var _next : FXmlNode?
    protected var _child : MutableList<FXmlNode>
    constructor()
    {
        _tabCount = 0
        _filePath = ""
        _version = "\"1\""
        _encoding = "\"UTF-8\""
        _startTag = null
        _endTag = null
        _text = null
        _parent = null
        _prev = null
        _next = null
        _child = ArrayList()
    }
    constructor(buffArray: MutableList<String>, parent: FXmlNode?, prev: FXmlNode?, next: FXmlNode?)
    {
        _tabCount = 0
        _filePath = ""
        _version = "\"1\""
        _encoding = "\"UTF-8\""
        _startTag = null
        _endTag = null
        _text = null
        _parent = null
        _prev = null
        _next = null
        _child = ArrayList()
        setThis(buffArray, parent, prev, next)
    }
    constructor(tag : String, text : String, parent : FXmlNode?, prev : FXmlNode?)
    {
        _tabCount = 0
        _filePath = ""
        _filePath = ""
        _version = "\"1\""
        _encoding = "\"UTF-8\""
        _startTag = tag
        _endTag = tag
        _text = text
        _parent = parent
        _prev = prev
        _next = null
        _child = ArrayList()
    }

    protected fun setThis(buffArray : MutableList<String>, parent : FXmlNode?, prev : FXmlNode?, next : FXmlNode?)
    {
        _parent = parent
        _prev = prev
        _next = next
        if (buffArray.size == 0)
        {
            return
        }

        var prefix = 0
        val startTagSPos = buffArray[prefix].indexOf(_stx)
        val startTagEPos = buffArray[prefix].indexOf(_etx)
        _startTag = buffArray[prefix].substring(startTagSPos + 1, startTagEPos)
        val startTagPos = prefix
        var endTagPos = -1
        prefix++

        for (i in prefix until buffArray.size)
        {
            if (buffArray[i].contains("${_stx}/$_startTag${_etx}"))
            {
                endTagPos = i
                val endTagSPos = buffArray[i].indexOf(_stx)
                val endTagEPos = buffArray[i].indexOf(_etx)
                _endTag = buffArray[i].substring(endTagSPos + 2, endTagEPos)
                break
            }
        }

        if (startTagEPos != buffArray[startTagPos].lastIndex)
        {
            _text = buffArray[startTagPos].substring(startTagEPos + 1, buffArray[startTagPos].lastIndex + 1)
        }

        if (endTagPos + 1 != buffArray.size)
        {
            _next = FXmlNode(buffArray.subList(endTagPos + 1, buffArray.size), parent, this, null)
        }

        if (startTagPos + 1 != endTagPos)
        {
            _child.add(FXmlNode(buffArray.subList(startTagPos + 1, endTagPos), this, null, null))
            if (_child[0]._next != null)
            {
                var child = _child[0]._next
                while (child != null)
                {
                    _child.add(child)
                    child = child._next
                }
            }
        }
    }

    fun getStartTag() : String? = _startTag
    fun getEndTag() : String? = _endTag
    fun getText() : String? = _text
    fun getParent() : FXmlNode? = _parent
    fun getFirst() : FXmlNode?
    {
        if (_prev != null)
        {
            return _prev!!.getFirst()
        }

        return this
    }
    fun getLast() : FXmlNode?
    {
        if (_next != null)
        {
            return _next!!.getLast()
        }

        return this
    }
    fun getPrev() : FXmlNode? = _prev
    fun getPrev(xpath : String) : FXmlNode?
    {
        if (_prev != null)
        {
            if (_prev!!._startTag == xpath)
            {
                return _prev
            }
            else
            {
                return _prev!!.getPrev(xpath)
            }
        }

        return _prev
    }
    fun getNext() : FXmlNode? =_next
    fun getNext(xpath : String) : FXmlNode?
    {
        if (_next != null)
        {
            if (_next!!._startTag == xpath)
            {
                return _next
            }
            else
            {
                return _next!!.getNext(xpath)
            }
        }

        return _next
    }
    fun getChildSize() : Int = _child.size
    fun getChild() : MutableList<FXmlNode>
    {
        return _child
    }
    fun getChild(index : Int) : FXmlNode?
    {
        if (index < 0 || index >= _child.size)
        {
            return null
        }

        return _child[index]
    }
    fun getChild(xpath : String) : FXmlNode?
    {
        return _child.find { it._startTag == xpath }
    }
    fun getNode() : FXmlNode?
    {
        return this
    }
    fun getNode(xpath : String) : FXmlNode?
    {
        if (_startTag == xpath)
        {
            return this
        }
        if (_child.size > 0)
        {
            _child.find { it._startTag == xpath
            }?.run { return this
            } ?: kotlin.run {
                for (i in _child)
                {
                    i.getNode(xpath)?.run {
                        return this
                    }
                }
            }
        }
        if (_next != null)
        {
            return if (_next!!._startTag == xpath)
            {
                _next
            } else
            {
                _next!!.getNode(xpath)
            }
        }

        return null
    }

    fun setTag(xpath : String)
    {
        _startTag = xpath
        _endTag = xpath
    }
    fun setLastChildText(xpath : String, text : String) : Boolean
    {
        if (_child.size > 0)
        {
            _child.find { it._startTag == xpath
            }?.run {
                this.setText(text)
                return true
            } ?: kotlin.run {
                for (i in _child)
                {
                    if (i.setLastChildText(xpath, text))
                    {
                        return true
                    }
                }

                return false
            }
        }
        else
        {
            _text = text
            return true
        }
    }
    fun setLastChild(xmlNode : FXmlNode) : Boolean
    {
        if (_child.size > 0)
        {
            for (i in _child)
            {
                if (i.setLastChild(xmlNode))
                {
                    return true
                }
            }

            return false
        }
        else
        {
            xmlNode._parent = this
            _child.add(xmlNode)
            return true
        }
    }
    fun setNode(xmlNode : FXmlNode)
    {
        _tabCount = xmlNode._tabCount
        _filePath = xmlNode._filePath
        _version = xmlNode._version
        _encoding = xmlNode._encoding
        _startTag = xmlNode.getStartTag()
        _endTag = xmlNode.getEndTag()
        _text = xmlNode.getText()
        _parent = xmlNode.getParent()
        _prev = xmlNode.getPrev()
        _next = xmlNode.getNext()
        _child = xmlNode.getChild()
    }

    fun appendChild(xmlNode : FXmlNode)
    {
        if (_child.size > 0)
        {
            _child.last()._next = xmlNode
        }
        _child.add(xmlNode)
    }
    fun setText(text : String)
    {
        _text = text
    }

    protected fun getString(tab : Int) : String
    {
        if (_startTag == null)
        {
            return ""
        }
        _tabCount = tab
        var ret = "$_stx$_startTag$_etx"
        if (_text != null)
        {
            ret += _text
        }
        if (_child.size > 0)
        {
            _tabCount++
            for (i in _child)
            {
                ret += "$_ent${addTab()}${i.getString(_tabCount)}"
            }
        }

        ret += if (_text != null)
        {
            "$_stx/$_endTag$_etx"
        }
        else
        {
            _tabCount--
            "$_ent${addTab()}$_stx/$_endTag$_etx"
        }

        return ret
    }
    protected fun addTab() : String
    {
        var ret = ""
        for (i in 0 until _tabCount)
        {
            ret += _tab
        }
        return ret
    }
}