package magellan.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import magellan.library.Alliance;
import magellan.library.AllianceGroup;
import magellan.library.Border;
import magellan.library.Building;
import magellan.library.CoordinateID;
import magellan.library.EntityID;
import magellan.library.Faction;
import magellan.library.GameData;
import magellan.library.ID;
import magellan.library.IntegerID;
import magellan.library.Island;
import magellan.library.Item;
import magellan.library.LuxuryPrice;
import magellan.library.Message;
import magellan.library.Region;
import magellan.library.Ship;
import magellan.library.Skill;
import magellan.library.Spell;
import magellan.library.StringID;
import magellan.library.Unit;
import magellan.library.UnitContainer;
import magellan.library.UnitID;
import magellan.library.gamebinding.EresseaConstants;
import magellan.library.impl.MagellanBuildingImpl;
import magellan.library.impl.MagellanShipImpl;
import magellan.library.impl.SpellBuilder;
import magellan.library.io.GameDataReader;
import magellan.library.rules.BuildingType;
import magellan.library.rules.EresseaDate;
import magellan.library.rules.ItemCategory;
import magellan.library.rules.ItemType;
import magellan.library.rules.ShipType;
import magellan.library.rules.SimpleDate;
import magellan.library.rules.SkillType;
import magellan.library.utils.CollectionFactory;
import magellan.library.utils.MagellanFactory;
import magellan.library.utils.Resources;

/**
 * A helper class for creating reports for tests.
 *
 * @author ...
 * @version 1.0, Sep 26, 2012
 */
public class GameDataBuilder {

  private static final int BASE_ROUND = 901;
  private String gameName = ERESSEA;
  private Locale locale = Locale.GERMAN;
  private int factionSortIndex = 1;
  private int regionSortIndex = 1;
  private int allianceID = 1;

  public static final String ERESSEA = "Eressea";
  public static final String E3 = "E3";
  public static final String ATLANTIS = "Atlantis";

  private static int maxId = 0;

  /**
   * Creates a report at round {@value #BASE_ROUND}.
   *
   * @see #createSimplestGameData(int)
   */
  public GameData createSimplestGameData() throws Exception {
    return createSimplestGameData(BASE_ROUND);
  }

  /**
   * Creates a GameData object that is always postProcessed with one faction, one island, one
   * region, and one unit
   */
  public GameData createSimplestGameData(int round) throws Exception {
    return createSimplestGameData(round, true);
  }

  /**
   * Creates a GameData object that is always postProcessed with one faction, one island, one
   * region, and (if <code>addUnit</code>) one unit
   */
  public GameData createSimplestGameData(int round, boolean addUnit) throws Exception {
    return createSimplestGameData(getGameName(), round, addUnit, true);
  }

  /**
   * Creates a GameData object that is always postProcessed with one faction, one island, one
   * region, and (if <code>addUnit</code>) one unit
   */
  public GameData createSimplestGameData(String aGameName, int round, boolean addUnit)
      throws Exception {
    return createSimplestGameData(aGameName, round, addUnit, true);
  }

  /**
   * Creates a GameData object with one faction, one island, one region, and (if
   * <code>addUnit</code>) one unit.
   */
  public GameData createSimplestGameData(String aGameName, int round, boolean addUnit,
      boolean postProcess) throws Exception {
    final GameData data = new GameDataReader(null).createGameData(aGameName);

    if (gameName.equals(ATLANTIS)) {
      data.base = 10;
    } else {
      data.base = 36;
      // this is sadly needed
      // IDBaseConverter.setBase(data.base);
    }

    data.noSkillPoints = true;

    if (gameName.equals(ATLANTIS)) {
      data.setLocale(Locale.ENGLISH);
    } else {
      data.setLocale(Locale.GERMAN);
    }

    if (gameName.equals(ATLANTIS)) {
      data.setDate(new SimpleDate("July", "15"));
    } else {
      final EresseaDate ed = new EresseaDate(round);
      ed.setEpoch(2);
      data.setDate(ed);
    }

    // data.setCurTempID
    // data.mailTo
    // data.mailSubject

    final Faction faction;
    if (gameName.equals(ATLANTIS)) {
      faction = addFaction(data, "1", "Mooks", "Menschen", 1);
      faction.setLocale(Locale.ENGLISH);
    } else {
      faction = addFaction(data, "iLja", "Faction_867718", "Meermenschen", -1);
    }

    final Island island = addIsland(data, 1, "Island_1");

    final Region region_0_0 = addRegion(data, "0 0", "Region_0_0", "Ebene", -1);
    region_0_0.setIsland(island);

    if (addUnit) {
      addUnit(data, "1", "Unit_1", faction, region_0_0);
    }

    if (postProcess) {
      data.postProcess();
    }
    return data;
  }

  /**
   * Creates a GameData object with one unit which has Hiebwaffen 4 (+3), Segeln - (-3), Magie 4,
   * Steinbau -. in round {@link #BASE_ROUND}.
   */
  public GameData createSimpleGameData() throws Exception {
    return createSimpleGameData(BASE_ROUND);
  }

  /**
   * Creates a GameData object with one unit which has Hiebwaffen 4 (+3), Segeln - (-3), Magie 4,
   * Steinbau -.
   */
  public GameData createSimpleGameData(int round) throws Exception {
    return createSimpleGameData(round, true);
  }

  /**
   * Creates a GameData object where all units have Hiebwaffen 4 (+3), Segeln - (-3), Magie 4,
   * Steinbau -. Add a unit if <code>addUnit</code>.
   */
  public GameData createSimpleGameData(int round, boolean addUnit) throws Exception {
    return createSimpleGameData(getGameName(), round, addUnit);
  }

  /**
   * Creates a GameData object of the specified type where all units have Hiebwaffen 4 (+3), Segeln
   * - (-3), Magie 4, Steinbau -. Add a unit if <code>addUnit</code>.
   */
  public GameData createSimpleGameData(String aGameName, int round, boolean addUnit)
      throws Exception {
    final GameData data = createSimplestGameData(aGameName, round, addUnit, false);

    if (data.getUnits().size() > 0) {
      final Unit unit = data.getUnits().iterator().next();

      if (gameName.equals(ATLANTIS)) {
        addSkill(unit, "sword", 4, 3); // Hiebwaffen 4 (+3)
        // addSkill(unit, "", -1, -3); // Segeln - (-3)
        // addSkill(unit, "Magie", 4, 0, true); // Magie 4
        addSkill(unit, "quarrying", 0, 9); // Steinbau -
      } else {
        addSkill(unit, "Hiebwaffen", 4, 3); // Hiebwaffen 4 (+3)
        addSkill(unit, "Segeln", 0, -3); // Segeln - (-3)
        // addSkill(unit, "Magie", 4, 0, true); // Magie 4
        addSkill(unit, "Steinbau", 0, 9); // Steinbau -
      }
    }

    data.postProcess();
    return data;
  }

  /**
   * Add a faction with passwort to the given report.
   *
   * @param data
   * @param number
   * @param name Set to null to create a non-privileged faction
   * @param race
   * @param sortIndex Set to -1 to autoincrease
   * @return The new faction
   */
  public Faction addFaction(GameData data, String number, String name, String race, int sortIndex) {
    final EntityID id = EntityID.createEntityID(number, data.base);
    if (data.getFaction(id) != null)
      throw new IllegalArgumentException("faction exists");

    final Faction faction = MagellanFactory.createFaction(id, data);
    data.addFaction(faction);

    if (sortIndex > 0) {
      factionSortIndex = Math.max(sortIndex, ++factionSortIndex);
      faction.setSortIndex(sortIndex);
    } else {
      faction.setSortIndex(++factionSortIndex);
    }

    faction.setName(name != null ? name : ("Partei " + factionSortIndex));

    faction.setPassword(name);

    if (race != null) {
      faction.setType(data.getRules().getRace(StringID.create(race), true));
    }

    faction.setLocale(getLocale());

    return faction;
  }

  /**
   * Add the unknown faction.
   * 
   * @param data
   */
  public Faction addUnknownFaction(GameData data) {
    Faction faction = addFaction(data, "-1", null, "Mensch", -1);
    faction.setName("Parteigetarnte");
    return faction;
  }

  /**
   * Add the monster faction.
   * 
   * @param data
   */
  public Faction addMonsterFaction(GameData data) {
    Faction faction = addFaction(data, "ii", null, "Mensch", -1);
    faction.setName("Monster");
    return faction;
  }

  public Locale getLocale() {
    return locale;
  }

  /**
   * Sets the value of locale.
   *
   * @param locale The value for locale.
   */
  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  /**
   * Add an island to the report
   *
   * @param data
   * @param number
   * @param name
   * @return The new island.
   */
  public Island addIsland(GameData data, int number, String name) {
    final IntegerID id = IntegerID.create(number);

    final Island island = MagellanFactory.createIsland(id, data);
    data.addIsland(island);

    island.setName(name);

    return island;
  }

  /**
   * Adds or replaces a region to the report.
   *
   * @param data The report
   * @param coordinate as "1 0" or "2,3"
   * @param name
   * @param type RegionType, e.g., "Ebene"
   * @param sortIndex
   * @return The new region
   */
  public Region
      addRegion(GameData data, String coordinate, String name, String type, int sortIndex) {
    CoordinateID c = CoordinateID.parse(coordinate, " ");
    if (c == null) {
      c = CoordinateID.parse(coordinate, ",");
    }

    final Region region = MagellanFactory.createRegion(c, data);
    data.addRegion(region);

    if (name != null) {
      region.setName(name);
    } else {
      region.setName(type + "_" + c);
    }

    if (type != null) {
      region.setType(data.getRules().getRegionType(StringID.create(type), true));
    }

    if (sortIndex > 0) {
      region.setSortIndex(sortIndex);
      regionSortIndex = Math.max(sortIndex, ++regionSortIndex);
    } else {
      region.setSortIndex(++regionSortIndex);
    }

    return region;
  }

  /**
   * Calls {@link #addUnit(GameData, String, String, Faction, Region, boolean)} with generated
   * details. "well known" is set to <code>false</code>.
   *
   * @param data
   * @param name
   * @param region
   * @return the new unit
   */
  public Unit addUnit(GameData data, String name, Region region) {
    return addUnit(data, name, region, false);
  }

  /**
   * Calls {@link #addUnit(GameData, String, String, Faction, Region, boolean)} with generated
   * details.
   *
   * @param data
   * @param name
   * @param region
   * @param wellKnown
   * @return the new unit
   */
  public Unit addUnit(GameData data, String name, Region region, boolean wellKnown) {
    final String number = "g" + (data.getUnits().size() + 1);
    final Faction faction = data.getFactions().iterator().next();
    return addUnit(data, number, name, faction, region, wellKnown);
  }

  /**
   * Calls {@link #addUnit(GameData, String, String, Faction, Region, boolean)} with generated
   * details. "well known" is set to <code>true</code>.
   *
   * @param data
   * @param number
   * @param name
   * @param faction
   * @param region
   * @return the new unit
   */
  public Unit addUnit(GameData data, String number, String name, Faction faction, Region region) {
    return addUnit(data, number, name, faction, region, true);
  }

  /**
   * Adds a new unit with the given details to the report
   *
   * @param data
   * @param number
   * @param name
   * @param faction
   * @param region
   * @param wellKnown
   * @return The new unit.
   */
  public Unit addUnit(GameData data, String number, String name, Faction faction, Region region,
      boolean wellKnown) {
    final UnitID id = UnitID.createUnitID(number, data.base);

    final Unit unit = MagellanFactory.createUnit(id, data);
    if (data.getUnit(id) != null)
      throw new RuntimeException("Unit " + id + " already exists.");
    data.addUnit(unit);

    unit.setName(name);

    unit.setFaction(faction);

    unit.setRace(faction.getRace());
    unit.setRealRace(faction.getRace());

    unit.setRegion(region);

    if (wellKnown) {
      unit.setOrders(Collections.singleton(""));
      unit.setCombatStatus(EresseaConstants.CS_NOT);
    }
    return unit;
  }

  /**
   * Add a skill which was lost (unknown) to the given unit.
   */
  public Skill addLostSkill(Unit unit, String name, int oldLevel) {
    return addSkill(unit, name, -1, -oldLevel);
  }

  /**
   * Add a skill to the given unit.
   */
  public Skill addSkill(Unit unit, String name, int level) {
    return addSkill(unit, name, level, 0);
  }

  /**
   * Add a skill with change
   */
  public Skill addChangedSkill(Unit unit, String name, int level, int fromLevel) {
    if (fromLevel == level)
      throw new IllegalArgumentException("changed level " + level + " = fromLevel");
    return addSkill(unit, name, level, level - fromLevel);
  }

  protected Skill addSkill(Unit unit, String name, int level, int change) {

    final SkillType skt = unit.getData().getRules().getSkillType(StringID.create(name), true);
    final int raceBonus = unit.getRace().getSkillBonus(skt);
    final int points = Skill.getPointsAtLevel(level - raceBonus);

    final Skill skill =
        new Skill(skt, points, level, unit.getPersons(), unit.getData().noSkillPoints);

    skill.setChangeLevel(change);

    // skill.setLevelChanged(changed);

    unit.addSkill(skill);

    return skill;
  }

  /**
   * Creates a new message with the given text.
   */
  public static Message createMessage(String text) {
    // EINHEITSBOTSCHAFTEN
    // "Eine Botschaft von Kr�uterlager (ax1a): 'MessMach99?99?99!Wundsalbe!xxxx'"
    return MagellanFactory.createMessage(text);
  }

  /**
   * Adds a road to the given region
   *
   * @param region
   * @param direction
   * @param buildRatio
   * @return The new road
   */
  public Border addRoad(Region region, int direction, int buildRatio) {
    // GRENZE 1
    // "Stra�e";typ
    // 0;richtung
    // 100;prozent
    final Border road = MagellanFactory.createBorder(IntegerID.create(++maxId));

    road.setDirection(direction);
    // road.setDirectionName(region.getData().getGameSpecificStuff().getOrderChanger()
    // .getOrder(
    // getLocale(),
    // region.getData().getGameSpecificStuff().getMapMetric()
    // .toDirection(direction).getId()));
    road.setBuildRatio(buildRatio);
    road.setType("Stra�e");

    region.addBorder(road);

    return road;
  }

  /**
   * Add some default spells to the report.
   *
   * @param data
   */
  public static void addSpells(GameData data) {
    Unit mage = data.getUnits().iterator().next();
    Map<ID, Spell> spellMap = new HashMap<ID, Spell>();
    IntegerID id = IntegerID.create(999);
    SpellBuilder spell = new SpellBuilder(id, data);
    spell.setName("Hagel");
    spell.setLevel(5);
    spell.setType("combat");
    final Spell hail = spell.construct();
    data.addSpell(hail);
    spellMap.put(id, hail);

    id = IntegerID.create(998);
    spell = new SpellBuilder(id, data);
    spell.setName("Gro�es Fest");
    spell.setLevel(2);
    spell.setType("normal");
    spell.setSyntax("");
    final Spell feast = spell.construct();
    data.addSpell(feast);
    spellMap.put(id, feast);

    id = IntegerID.create(997);
    spell = new SpellBuilder(id, data);
    spell.setName("Schild");
    spell.setLevel(4);
    spell.setType("normal");
    spell.setSyntax("u");
    final Spell shield = spell.construct();
    data.addSpell(shield);
    spellMap.put(id, shield);

    mage.setSpells(spellMap);
  }

  /**
   * Adds the specified item to the unit.
   */
  public void addItem(GameData data, Unit unit, String item, int amount) {
    unit.addItem(new Item(data.getRules().getItemType(item, true), amount));
  }

  /**
   * Adds a building to the report.
   *
   * @param data
   * @param region
   * @param id
   * @param type {@link BuildingType}
   * @param name
   * @param size
   */
  public Building addBuilding(GameData data, Region region, String id, String type, String name,
      int size) {
    Building building = new MagellanBuildingImpl(EntityID.createEntityID(id, data.base), data);
    building.setName(name);
    building.setRegion(region);
    building.setType(data.getRules().getBuildingType(type));
    building.setSize(size);

    region.addBuilding(building);
    data.addBuilding(building);

    return building;
  }

  /**
   * Adds a ship to the report.
   *
   * @param data
   * @param region
   * @param id
   * @param type {@link ShipType}
   * @param name
   * @param size
   */
  public Ship addShip(GameData data, Region region, String id, String type, String name, int size) {
    Ship ship = new MagellanShipImpl(EntityID.createEntityID(id, data.base), data);
    ship.setName(name);
    ship.setRegion(region);
    ship.setType(data.getRules().getShipType(type));
    ship.setSize(size);
    ship.setCapacity(ship.getShipType().getCapacity() * 100);

    region.addShip(ship);
    data.addShip(ship);

    return ship;
  }

  /**
   * Set some default luxury prices.
   *
   * @param region
   * @param buy The buyable luxury good
   */
  public void setPrices(Region region, String buy) {
    Map<StringID, LuxuryPrice> prices = region.getPrices();
    ItemCategory cat = region.getData().getRules().getItemCategory(EresseaConstants.C_LUXURIES);
    if (cat == null)
      throw new IllegalStateException("no luxuries known");
    int pr = 4;
    for (ItemType type : region.getData().getRules().getItemTypes())
      if (type.getCategory() != null && type.getCategory().equals(cat)) {
        if (prices == null) {
          prices = CollectionFactory.<StringID, LuxuryPrice> createSyncOrderedMap(8, .9f);
        }
        if (type.getName().equals(buy)) {
          prices.put(type.getID(), new LuxuryPrice(type, -(pr++)));
        } else {
          prices.put(type.getID(), new LuxuryPrice(type, pr++));
        }
      }
    region.setPrices(prices);
  }

  /**
   * Adds a HELP state from faction to ally. New state is ORed with existing help states.
   */
  public void addAlliance(Faction faction, Faction ally, int state) {
    Map<EntityID, Alliance> allies1 = faction.getAllies();
    if (allies1 == null) {
      allies1 = CollectionFactory.createMap();
      faction.setAllies(allies1);
    }
    Alliance alliance = allies1.get(ally.getID());
    if (alliance == null) {
      alliance = new Alliance(ally);
      allies1.put(ally.getID(), alliance);
    }
    alliance.addState(state);
  }

  /**
   * Add an (E3 style) alliance with given leader.
   * 
   */
  public AllianceGroup addAlliance(GameData data, Faction leader) {
    final EntityID id = EntityID.createEntityID(++allianceID, data.base);
    final AllianceGroup alliance = new AllianceGroup(id);
    alliance.setName("Allianz " + allianceID);
    alliance.setLeader(leader.getID());
    data.addAllianceGroup(alliance);
    alliance.addFaction(leader);
    leader.setAlliance(alliance);

    return alliance;
  }

  /**
   * Add a faction to an alliance.
   * 
   */
  public AllianceGroup addToAlliance(AllianceGroup alliance, Faction faction) {
    alliance.addFaction(faction);
    faction.setAlliance(alliance);

    return alliance;
  }

  /**
   * Returns the value of gameName.
   *
   * @return Returns gameName.
   */
  public String getGameName() {
    return gameName;
  }

  /**
   * Sets the value of gameName.
   *
   * @param gameName The value for gameName.
   */
  public void setGameName(String gameName) {
    this.gameName = gameName;
  }

  public static void setNullResources(boolean setNull) {
    Resources.setNullResource(setNull);
  }

  /**
   * Adds a unit to a building or ship and makes it the owner if
   *
   * @param unit
   * @param uc
   */
  public void addTo(Unit unit, UnitContainer uc) {
    if (uc instanceof Building) {
      unit.setBuilding((Building) uc);
    } else if (uc instanceof Ship) {
      unit.setShip((Ship) uc);
    } else
      throw new RuntimeException(uc + " is neither building nor ship.");
    if (uc.units().size() == 1) {
      uc.setOwner(unit);
      uc.setOwnerUnit(unit);
    }
  }

}
