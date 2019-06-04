/*******************************************************************************
 * Copyright 2019 T145
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package T145.metaltransport.items;

import org.apache.commons.lang3.StringUtils;

import T145.metaltransport.api.constants.RegistryMT;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMod extends Item {

	protected final IStringSerializable[] types;
	protected final CreativeTabs tab;

	public ItemMod(ResourceLocation resource, IStringSerializable[] types, CreativeTabs tab) {
		setHasSubtypes(types != null);

		if (hasSubtypes) {
			// validate the types we'll use to build items out of
			try {
				for (IStringSerializable type : types) {
					if (type == null) {
						throw new NullPointerException(" [ItemMod] Cannot build items out of null objects in \"types\"!");
					}
				}
			} catch (NullPointerException err) {
				RegistryMT.LOG.catching(err);
				RegistryMT.LOG.error(String.format(" [ItemMod] %s", types.toString()));
			}
		}

		this.types = types;
		this.tab = tab;

		setRegistryName(resource);
		setTranslationKey(resource.toString());
		setCreativeTab(tab);
	}

	public ItemMod(ResourceLocation resource, CreativeTabs tab) {
		this(resource, null, tab);
	}

	public IStringSerializable[] getTypes() {
		return types;
	}

	@Override
	public String getCreatorModId(ItemStack stack) {
		return tab.getTabLabel().replace("itemGroup.", StringUtils.EMPTY);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		if (hasSubtypes) {
			return String.format("%s.%s", super.getTranslationKey(), types[stack.getMetadata()].getName());
		}
		return super.getTranslationKey(stack);
	}

	@SideOnly(Side.CLIENT)
	public void prepareCreativeTab(NonNullList<ItemStack> items) {
		for (int meta = 0; meta < types.length; ++meta) {
			items.add(new ItemStack(this, 1, meta));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.tab == tab) {
			if (hasSubtypes) {
				prepareCreativeTab(items);
			} else {
				items.add(new ItemStack(this));
			}
		}
	}
}
