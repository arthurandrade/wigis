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

package net.wigis.graph.data.utilities;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;

import org.archive.io.ArchiveRecord;
import org.archive.io.warc.WARCReader;
import org.archive.io.warc.WARCReaderFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class WarcExtract.
 * 
 * @author Brynjar Gretarsson
 */
public class WarcExtract
{

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParseException
	 *             the parse exception
	 */
	public static void main( String[] args ) throws IOException, ParseException
	{
		String inputWarcFile = "/pkg/heritrix-1.14.3/jobs/www.ucsb.edu-20100414001731286/warcs/IAH-20100414001731-00000-ilab-115.cs.ucsb.edu.warc.gz";
		File file = new File( inputWarcFile );

		WARCReader wr = WARCReaderFactory.get( file );

		Iterator<ArchiveRecord> i = wr.iterator();
		ArchiveRecord ar;
		while( ( ar = i.next() ) != null )
		{
			try
			{
				ar.dump( System.out );
				System.out.println();
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
		// // open our gzip input stream
		// GZIPInputStream gzInputStream = new GZIPInputStream( new
		// FileInputStream( file ) );
		//
		// // cast to a data input stream
		// DataInputStream inStream = new DataInputStream( gzInputStream );
		//
		// // iterate through our stream
		// WarcRecord thisWarcRecord;
		// while( ( thisWarcRecord = WarcRecord.readNextWarcRecord( inStream ) )
		// != null )
		// {
		// // see if it's a response record
		// if( thisWarcRecord.getHeaderRecordType().equals( "response" ) )
		// {
		// // it is - create a WarcHTML record
		// WarcHTMLResponseRecord htmlRecord = new WarcHTMLResponseRecord(
		// thisWarcRecord );
		// // get our TREC ID and target URI
		// String thisTRECID = htmlRecord.getTargetTrecID();
		// String thisTargetURI = htmlRecord.getTargetURI();
		// // print our data
		// System.out.println( thisTRECID + " : " + thisTargetURI );
		// }
		//			
		// System.out.println( thisWarcRecord.getHeaderRecordType() );
		// }
		//
		// inStream.close();
	}
}
