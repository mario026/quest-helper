/*
 * Copyright (c) 2020, Mario Hendriks
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.quests.landofthegoblin;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.TileStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;


@QuestDescriptor(
	quest = QuestHelperQuest.LAND_OF_THE_GOBLINS
)
public class LandOfTheGoblins extends BasicQuestHelper
{
	//Items Required
	ItemRequirement blackDye, lightSource, toadflaxPotionUnf, goblinMail, yellowDye, blueDye,
		orangeDye, purpleDye, fishingRod, rawSlimyEel, coins;
	//Items Recommended
	ItemRequirement tinderbox, dorgeshKaanSpheres,
		fairyRingItems, skillNecklace, combatNecklace, teleLumbridge, teleDraynor, explorersRing, salveAmulet, combatGear;

	Requirement inDorgeshuun, inBelowFishingGuild;

	QuestStep talkToGrubfoot, enterTheCity, talkToZanik, headToTheTemple, talkToTheGuard;

	//Zones
	Zone dorgeshaan, belowFishingGuild;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();
		steps.put(0, talkToGrubfoot);

		ConditionalStep grubsDream = new ConditionalStep(this, enterTheCity);
		grubsDream.addStep(inDorgeshuun, talkToZanik);

		steps.put(1, grubsDream);

		ConditionalStep imposterGoblin = new ConditionalStep(this, headToTheTemple);
		imposterGoblin.addText("Make sure u have enough inventory space to unequip all of your items.");
		grubsDream.addStep(inBelowFishingGuild, talkToTheGuard);

		steps.put(2, imposterGoblin);

		return steps;
	}

	public void setupItemRequirements()
	{
		//Items required
		lightSource = new ItemRequirement("A light source", ItemCollections.getLightSources());
		toadflaxPotionUnf = new ItemRequirement("Toadflax potion (unf)", ItemID.TOADFLAX_POTION_UNF);
		goblinMail = new ItemRequirement("Goblin Mail", ItemID.GOBLIN_MAIL);
		goblinMail.canBeObtainedDuringQuest();
		yellowDye = new ItemRequirement("Yellow dye", ItemID.YELLOW_DYE);
		blueDye = new ItemRequirement("Yellow dye", ItemID.BLUE_DYE);
		orangeDye = new ItemRequirement("Yellow dye", ItemID.ORANGE_DYE);
		purpleDye = new ItemRequirement("Yellow dye", ItemID.PURPLE_DYE);
		blackDye = new ItemRequirement("Black dye", ItemID.BLACK_DYE);
		blackDye.setTooltip("An empty vial and a pestle and mortar if making the black dye during the quest");
		fishingRod = new ItemRequirement("Fishing rod", ItemID.FISHING_ROD);
		rawSlimyEel = new ItemRequirement("Raw slimy eel", ItemID.RAW_SLIMY_EEL);
		coins = new ItemRequirement("Coins", ItemID.COINS, 5);

		//Recommended
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		tinderbox.setTooltip("Only if not using a non-extinguishable light source");
		dorgeshKaanSpheres = new ItemRequirement("Dorgesh-kaan Spheres", ItemID.DORGESHKAAN_SPHERE, 2);
		dorgeshKaanSpheres.canBeObtainedDuringQuest();
		fairyRingItems = new ItemRequirement("Dramen or lunar staff", ItemCollections.getFairyStaff());
		skillNecklace = new ItemRequirement("Skills Necklace", ItemCollections.getSkillsNecklaces());
		combatNecklace = new ItemRequirement("Combat Necklace", ItemCollections.getCombatBracelets());
		teleLumbridge = new ItemRequirement("Teleports to Lumbridge.", ItemID.LUMBRIDGE_TELEPORT);
		teleDraynor = new ItemRequirement("Teleports to Draynor Village", ItemCollections.getAmuletOfGlories());
		explorersRing = new ItemRequirement("Explorers Ring (3) or above.", ItemID.EXPLORERS_RING_3);
		explorersRing.addAlternates(ItemID.EXPLORERS_RING_4);
		salveAmulet = new ItemRequirement("Salve Amulet(e) or normal Salve Amulet will also work.", ItemID.SALVE_AMULET_E);
		salveAmulet.addAlternates(ItemID.SALVE_AMULET);
		combatGear = new ItemRequirement("Combat gear and food", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getArmour());
	}

	public void loadZones()
	{
		dorgeshaan = new Zone(new WorldPoint(2749, 5374, 0), new WorldPoint(2688, 5281, 0));
		belowFishingGuild = new Zone(new WorldPoint(2623, 9791, 0), new WorldPoint(2560, 9854, 0));
	}

	public void setupConditions()
	{
		inDorgeshuun = new ZoneRequirement(dorgeshaan);
		inBelowFishingGuild = new ZoneRequirement(belowFishingGuild);
	}

	public void setupSteps()
	{
		talkToGrubfoot = new NpcStep(this, NpcID.GRUBFOOT_11255, "Talk to Grubfoot in Dorgeshuun Mines to start the quest.");
		talkToGrubfoot.addDialogStep("Yes.");
		enterTheCity = new ObjectStep(this, ObjectID.DOOR_6919, new WorldPoint(3317, 9601, 0), "Enter the cty of Dorgeshuun");
		talkToZanik = new NpcStep(this, NpcID.ZANIK_11260, new WorldPoint(2705, 5363, 0), "Take Grubfoot to Zanik, who can be found in Oldak's workshop in the north-west corner of the city.");
		talkToZanik.addDialogStep("SO why have you come to talk to Zanik?");
		talkToZanik.addDialogStep("What was this new dream?");
		talkToZanik.addDialogStep("It's just a dream. It doesnt mean anything.");
		talkToZanik.addDialogStep("I'm ready.");

		headToTheTemple = new TileStep(this, new WorldPoint(2581, 9849, 0), "Head northwest to the temple entrance stairs blocked by two goblin guards.");
		headToTheTemple.addText("Pick a black mushroom if you don't already have black dye.");
		talkToTheGuard = new NpcStep(this, NpcID.GOBLIN_GUARD_11314, "Talk to the guards for Zanik to pass.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(lightSource);
		reqs.add(toadflaxPotionUnf);
		reqs.add(goblinMail);
		reqs.add(yellowDye);
		reqs.add(blueDye);
		reqs.add(orangeDye);
		reqs.add(purpleDye);
		reqs.add(blackDye);
		reqs.add(fishingRod);
		reqs.add(rawSlimyEel);
		reqs.add(coins);
		reqs.add(combatGear);

		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(tinderbox);
		reqs.add(dorgeshKaanSpheres);
		reqs.add(fairyRingItems);
		reqs.add(skillNecklace);
		reqs.add(combatNecklace);
		reqs.add(teleLumbridge);
		reqs.add(teleDraynor);
		reqs.add(explorersRing);
		reqs.add(salveAmulet);
		return reqs;
	}


	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Snothead (level 32)");
		reqs.add("Snailfeet (level 56)");
		reqs.add("Snailfeet (level 56)");
		reqs.add("Redeyes (level 121)");
		reqs.add("Strongbones (level 184)");

		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(new ExperienceReward(Skill.AGILITY, 8000), new ExperienceReward(Skill.FISHING, 8000), new ExperienceReward(Skill.THIEVING, 8000), new ExperienceReward(Skill.HERBLORE, 8000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(new UnlockReward("Access to the Goblin Temple (with an altar to recharge prayer points)"), new UnlockReward("Access to Yu'biusk, using fairy ring code blq"), new UnlockReward("Ability to purchase Plain of mud spheres that transports you to the Goblin Cave"), new UnlockReward("Ability to make goblin potions."));
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.AGILITY, 38, false));
		reqs.add(new SkillRequirement(Skill.FISHING, 40, false));
		reqs.add(new SkillRequirement(Skill.THIEVING, 45, false));
		reqs.add(new SkillRequirement(Skill.HERBLORE, 48, false));
		reqs.add(new QuestRequirement(QuestHelperQuest.ANOTHER_SLICE_OF_HAM, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.DEATH_TO_THE_DORGESHUUN, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.THE_LOST_TRIBE, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.GOBLIN_DIPLOMACY, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.RUNE_MYSTERIES, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.THE_GIANT_DWARF, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.THE_DIG_SITE, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.FISHING_CONTEST, QuestState.FINISHED));
		reqs.add(new CombatLevelRequirement(65));
		return reqs;
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToGrubfoot)));
		allSteps.add(new PanelDetails("Grubfoot's dream", Arrays.asList(enterTheCity, talkToZanik)));
		allSteps.add(new PanelDetails("Impostor Among Goblins", Arrays.asList(headToTheTemple),  toadflaxPotionUnf, skillNecklace));

		return allSteps;
	}
}
