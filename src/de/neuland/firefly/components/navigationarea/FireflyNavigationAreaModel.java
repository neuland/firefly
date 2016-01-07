/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.neuland.firefly.components.navigationarea;

import de.hybris.platform.cockpit.components.navigationarea.DefaultNavigationAreaModel;
import de.hybris.platform.cockpit.session.impl.AbstractUINavigationArea;

import de.neuland.firefly.session.impl.FireflyNavigationArea;


/**
 * Firefly navigation area model.
 */
public class FireflyNavigationAreaModel extends DefaultNavigationAreaModel
{
	public FireflyNavigationAreaModel()
	{
		super();
	}

	public FireflyNavigationAreaModel(final AbstractUINavigationArea area)
	{
		super(area);
	}

	@Override
	public FireflyNavigationArea getNavigationArea()
	{
		return (FireflyNavigationArea) super.getNavigationArea();
	}
}
