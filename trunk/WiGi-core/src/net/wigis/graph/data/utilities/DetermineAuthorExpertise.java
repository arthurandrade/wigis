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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.dnv.DNVEntity;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.Vector3D;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class DetermineAuthorExpertise.
 * 
 * @author Brynjar Gretarsson
 */
public class DetermineAuthorExpertise
{

	/**
	 * Determine expertise.
	 * 
	 * @param directory
	 *            the directory
	 * @param graphFile
	 *            the graph file
	 * @param mappingFile
	 *            the mapping file
	 */
	public static void determineExpertise( String directory, String graphFile, String mappingFile )
	{
		DNVGraph graph = new DNVGraph( directory + graphFile );
		determineExpertise( directory, graph, mappingFile );
	}

	/**
	 * Determine expertise.
	 * 
	 * @param directory
	 *            the directory
	 * @param graph
	 *            the graph
	 * @param mappingFile
	 *            the mapping file
	 */
	public static void determineExpertise( String directory, DNVGraph graph, String mappingFile )
	{
		List<String> colorList = getColorList();

		Map<String, List<DNVNode>> nodesByAuthor = new HashMap<String, List<DNVNode>>();
		List<DNVNode> tempList;
		String author;
		for( DNVEntity node : graph.getNodesByType( 0, "document" ).values() )
		{
			author = node.getProperty( "PROGRAM_OFFICER_NAME" );
			if( author != null )
			{
				tempList = nodesByAuthor.get( author );
				if( tempList == null )
				{
					tempList = new LinkedList<DNVNode>();
					nodesByAuthor.put( author, tempList );
				}
				tempList.add( (DNVNode)node );
			}
		}

		Map<String, Map<String, Integer>> authorToExpertiseCount = new HashMap<String, Map<String, Integer>>();
		for( String auth : nodesByAuthor.keySet() )
		{
			tempList = nodesByAuthor.get( auth );
			Map<String, Integer> expertiseCount = authorToExpertiseCount.get( auth );
			if( expertiseCount == null )
			{
				expertiseCount = new HashMap<String, Integer>();
				authorToExpertiseCount.put( auth, expertiseCount );
			}
			for( DNVNode node : tempList )
			{
				for( DNVNode neighbor : node.getNeighbors() )
				{
					if( neighbor.getType().equals( "topic" ) )
					{
						Integer count = expertiseCount.get( neighbor.getLabel() );
						if( count == null )
						{
							count = 0;
						}
						count++;
						expertiseCount.put( neighbor.getLabel(), count );
					}
				}
			}
		}

		Integer maxCount = 0;
		String maxValue = "";
		Integer count = 0;
		Map<String, Integer> expertiseCount;
		Map<String, Vector3D> expertiseToColor = new HashMap<String, Vector3D>();
		Vector3D color;
		StringBuilder output = new StringBuilder();
		output.append( "All Departments:,Field Mapping:,Color Mapping\n" );
		for( String auth : authorToExpertiseCount.keySet() )
		{
			maxCount = 0;
			maxValue = "";
			expertiseCount = authorToExpertiseCount.get( auth );
			for( String value : expertiseCount.keySet() )
			{
				count = expertiseCount.get( value );
				if( count > maxCount )
				{
					maxCount = count;
					maxValue = value;
				}
			}

			color = expertiseToColor.get( maxValue );
			if( color == null )
			{
				if( colorList.size() > 0 )
				{
					color = GraphFunctions.convertColor( colorList.remove( 0 ) );
				}
				else
				{
					color = new Vector3D( (float)Math.random(), (float)Math.random(), (float)Math.random() );
				}
				expertiseToColor.put( maxValue, color );
			}

			output.append( "\"" ).append( auth ).append( "\",\"" ).append( maxValue ).append( "\",\"" ).append( color.toHexColor() ).append( "\"\n" );
		}

		writeFile( directory + mappingFile, output.toString() );
	}

	/**
	 * Write file.
	 * 
	 * @param file
	 *            the file
	 * @param output
	 *            the output
	 */
	private static void writeFile( String file, String output )
	{
		try
		{
			FileWriter fw = new FileWriter( new File( file ) );
			BufferedWriter bw = new BufferedWriter( fw );
			bw.write( output );
			bw.close();
			fw.close();
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gets the color list.
	 * 
	 * @return the color list
	 */
	public static List<String> getColorList()
	{
		List<String> colorList = new LinkedList<String>();

		colorList.add( "#FF0000" );
		colorList.add( "#00FF00" );
		colorList.add( "#0000FF" );
		colorList.add( "#00FFFF" );
		colorList.add( "#FF00FF" );
		colorList.add( "#FFFF00" );
		colorList.add( "#FFFFFF" );
		colorList.add( "#800000" );
		colorList.add( "#008000" );
		colorList.add( "#000080" );
		colorList.add( "#008080" );
		colorList.add( "#800080" );
		colorList.add( "#808000" );
		colorList.add( "#808080" );
		colorList.add( "#000000" );

		return colorList;
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main( String args[] )
	{
		GraphsPathFilter gpf = new GraphsPathFilter();
		try
		{
			gpf.init( null );
		}
		catch( ServletException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String directory = Settings.GRAPHS_PATH + "topicVisualizations/NSF/";
		String graphFile = "NSF_0.1_2_false_false_1.0_false_false_false.dnv";
		String mappingFile = "departmentToColorMapping.txt";

		determineExpertise( directory, graphFile, mappingFile );
	}

}
