package com.hwjustjava.app;

class Menu
{
    private java.util.HashMap<String, IItem> Items;

    public Menu()
    {
        Items = new java.util.HashMap<String, IItem>();
    }

    public Menu(String CsvFilePath) throws InvalidMenuCsvException
    {
        Items = new java.util.HashMap<String, IItem>();
        try {
            FromCsvFile(CsvFilePath);
        } catch (InvalidMenuCsvException e) {
            System.out.println("Error loading menu: " + e.getMessage());
            throw e;
        }
    }

    public void FromCsvFile(String CsvFilePath) throws InvalidMenuCsvException
    {
        java.io.BufferedReader buff = null;
        String row[] = new String[4];

        try {
            buff = new java.io.BufferedReader(new java.io.FileReader(CsvFilePath));
            String inputLine = buff.readLine();
            while (inputLine != null)
            {
                row = inputLine.split(",");
                if (row.length == 0) {
                    inputLine = buff.readLine();
                    continue;
                }
                if (row.length != 5)
                    throw new InvalidMenuCsvException("Invalid number of rows");
                float cost = Float.parseFloat(row[1].trim());
                float PreparationTime = Float.parseFloat(row[4].trim());
                try {
                    IItem item = CreateItem(row[3].trim(), row[0].trim(), row[2].trim(), cost, PreparationTime);
                    Items.put(item.GetID(), item);
                } catch (InvalidItemException e) {
                    throw new InvalidMenuCsvException("Invalid item: " + e.getMessage());
                }
                inputLine = buff.readLine();
            }
        } catch (java.io.FileNotFoundException e) {
            throw new InvalidMenuCsvException("File not found: " + CsvFilePath);
        } catch (java.io.IOException e) {
            throw new InvalidMenuCsvException("Error reading file");
        } catch (NumberFormatException e) {
            throw new InvalidMenuCsvException("Number format Exception");
        }
    }

    public IItem CreateItem(String Category, String ItemID, String Description, float cost, float PreparationTime) throws InvalidItemException
    {
        IItem item = null;

        if (Items.get(Category + "." + ItemID) != null)
        {
            throw new InvalidItemException(Category + "." + ItemID + " already exists.");
        }

        if (cost < 0)
        {
            throw new InvalidItemException("Cost must be positive.");
        }

        if (ItemID.isEmpty()) {
            throw new InvalidItemException("ItemID must not be empty.");
        }
        if (ItemID.length() > 50) {
            throw new InvalidItemException("ItemID must be 50 characters or lower.");
        }

        switch (Category) {
            case "Merchandise":
                item = new Merchandise(ItemID, Description, cost, PreparationTime);
                break;
            case "Food":
                item = new Food(ItemID, Description, cost, PreparationTime);
                break;
            case "HotBeverage":
                item = new HotBeverage(ItemID, Description, cost, PreparationTime);
                break;
            case "ColdBeverage":
                item = new ColdBeverage(ItemID, Description, cost, PreparationTime);
                break;
            default:
                throw new InvalidItemException(Category + " is invalid");
        }
        return item;
    }

    public java.util.List<IItem> GetItems()
    {
        java.util.LinkedList<IItem> items = new java.util.LinkedList<IItem>();

        for (IItem item : Items.values())
        {
            items.add(item);
        }
        return items;
    }

    public IItem GetItem(String ItemID) throws UnknownItemException
    {
        IItem item = Items.get(ItemID);

        if (item == null)
        {
            throw new UnknownItemException(ItemID + " is unknown");
        }
        try {
            return CreateItem(item.GetCategory(), item.GetID(), item.GetDescription(), item.GetCost(), item.GetPreparationTime());
        } catch (InvalidItemException e) {
            // This should never happend because the item was already created!
            throw new UnknownItemException("This Sloud Never Happend! (Menu.GetItem(string))");
        }
    }
}
