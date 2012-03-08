/******************************************************************************************************
 * Copyright (c) 2010, University of California, Santa Barbara
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * 
 *    * Redistributions of source code must retain the above copyright notice, this list of
 *      conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright notice, this list of
 *      conditions and the following disclaimer in the documentation and/or other materials 
 *      provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 *****************************************************************************************************/

package net.wigis.svetlin;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

public class __BingAPINameSpaceContext implements NamespaceContext
{
	static final String WEB_NAMESPACE = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/web";
	static final String API_NAMESPACE = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/element";
	static final String SPELL_NAMESPACE = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/spell";
	static final String RS_NAMESPACE = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/relatedsearch";
	static final String PB_NAMESPACE = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/phonebook";
	static final String MM_NAMESPACE = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/multimedia";
	static final String AD_NAMESPACE = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/ads";
	static final String IA_NAMESPACE = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/instantanswer";
	static final String NEWS_NAMESPACE = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/news";
	static final String ENCARTA_NAMESPACE = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/encarta";
	
	public String getNamespaceURI(String prefix)
	{
		if (prefix == null)
			throw new NullPointerException("Null prefix");
		else if ("api".equals(prefix))
			return API_NAMESPACE;
		else if ("web".equals(prefix))
			return WEB_NAMESPACE;
		else if ("mms".equals(prefix))
			return MM_NAMESPACE;
		return "";
	}
	
	public String getPrefix(String uri)
	{
		throw new UnsupportedOperationException();
	}
	
	@SuppressWarnings({ "rawtypes" })
	public Iterator getPrefixes(String arg0)
	{
		throw new UnsupportedOperationException();
	}
}
