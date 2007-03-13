/*******************************************************************************
 * Copyright (c) 2007 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.chart.viewer.internal;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.eclipse.birt.chart.factory.IDataRowExpressionEvaluator;
import org.eclipse.birt.chart.factory.RunTimeContext;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.script.IExternalContext;
import org.eclipse.birt.chart.style.IStyleProcessor;
import org.eclipse.birt.chart.viewer.internal.util.ChartImageManager;
import org.eclipse.birt.chart.viewer.internal.util.ChartWebHelper;
import org.eclipse.birt.chart.viewer.internal.util.ImageHTMLEmitter;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * 
 * Tag for generating chart image and HTML
 * 
 */
public class ChartRendererTag extends TagSupport
{

	private static final long serialVersionUID = 1417590567722837605L;

	private double width;

	private double height;

	private String renderURL;

	private String output = "PNG"; //$NON-NLS-1$

	private Object model;
	private Chart chartModel;

	private IDataRowExpressionEvaluator data;

	private IStyleProcessor styleProcessor;

	private RunTimeContext runtimeContext;

	private IExternalContext externalContext;

	public int doEndTag( ) throws JspException
	{
		try
		{
			if ( !ChartWebHelper.checkOutputType( output ) )
			{
				printError( "Specified output type(" + output + ") is invalid!" ); //$NON-NLS-1$ //$NON-NLS-2$
				return EVAL_PAGE;
			}

			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest( );

			if ( model instanceof String )
			{
				chartModel = ChartWebHelper.parseChart( (String) model );
				if ( chartModel == null )
				{
					printError( "Following file does not exist: " + model ); //$NON-NLS-1$
					return EVAL_PAGE;
				}
			}
			else if ( model instanceof Chart )
			{
				chartModel = (Chart) EcoreUtil.copy( (Chart) model );
			}

			if ( chartModel != null )
			{
				// Set size in chart model
				Bounds bounds = chartModel.getBlock( ).getBounds( );
				bounds.setWidth( width );
				bounds.setHeight( height );
			}
			else
			{
				printError( "No chart model" ); //$NON-NLS-1$
				return EVAL_PAGE;
			}

			ChartImageManager imageManager = new ChartImageManager( request.getSession( )
					.getId( ),
					chartModel,
					output,
					data,
					runtimeContext,
					getExternalContext( ),
					styleProcessor );
			File imageFile = imageManager.getImage( );
			String imageId = imageFile.getName( );
			imageId = imageId.substring( 0, imageId.lastIndexOf( '.' ) );

			pageContext.getOut( )
					.println( createEmitter( imageId,
							imageManager.getRelativeImageFolder( )
									+ "/" + imageFile.getName( ), imageManager.getImageMap( ) ).generateHTML( ) ); //$NON-NLS-1$
		}
		catch ( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace( );
		}
		return EVAL_PAGE;
	}

	private ImageHTMLEmitter createEmitter( String id, String src,
			String imageMap )
	{
		ImageHTMLEmitter emitter = new ImageHTMLEmitter( );
		emitter.ext = this.output;
		emitter.height = (int) this.height;
		emitter.width = (int) this.width;
		emitter.id = id;
		emitter.src = src;
		emitter.imageMap = imageMap;
		return emitter;
	}

	private void printError( String message ) throws IOException
	{
		pageContext.getOut( ).println( "Error: " + message ); //$NON-NLS-1$
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth( double width )
	{
		this.width = width;
	}

	/**
	 * @return the width
	 */
	public double getWidth( )
	{
		return width;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight( double height )
	{
		this.height = height;
	}

	/**
	 * @return the height
	 */
	public double getHeight( )
	{
		return height;
	}

	/**
	 * @param renderURL
	 *            the renderURL to set
	 */
	public void setRenderURL( String renderURL )
	{
		this.renderURL = renderURL;
	}

	/**
	 * @return the renderURL
	 */
	public String getRenderURL( )
	{
		return renderURL;
	}

	/**
	 * @param output
	 *            the output to set
	 */
	public void setOutput( String output )
	{
		this.output = output;
	}

	/**
	 * @return the output
	 */
	public String getOutput( )
	{
		return output;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel( Object model )
	{
		this.model = model;
	}

	/**
	 * @return the model
	 */
	public Object getModel( )
	{
		return model;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData( IDataRowExpressionEvaluator data )
	{
		this.data = data;
	}

	/**
	 * @return the data
	 */
	public IDataRowExpressionEvaluator getData( )
	{
		return data;
	}

	/**
	 * @param styleProcessor
	 *            the styleProcessor to set
	 */
	public void setStyleProcessor( IStyleProcessor styleProcessor )
	{
		this.styleProcessor = styleProcessor;
	}

	/**
	 * @return the styleProcessor
	 */
	public IStyleProcessor getStyleProcessor( )
	{
		return styleProcessor;
	}

	/**
	 * @param runtimeContext
	 *            the runtimeContext to set
	 */
	public void setRuntimeContext( RunTimeContext runtimeContext )
	{
		this.runtimeContext = runtimeContext;
	}

	/**
	 * @return the runtimeContext
	 */
	public RunTimeContext getRuntimeContext( )
	{
		return runtimeContext;
	}

	/**
	 * @param externalContext
	 *            the externalContext to set
	 */
	public void setExternalContext( IExternalContext externalContext )
	{
		this.externalContext = externalContext;
	}

	/**
	 * @return the externalContext
	 */
	public IExternalContext getExternalContext( )
	{
		return externalContext;
	}
}
