/* eslint-disable no-undef */
// Right click on the script name and hit "Run" to execute
const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("StockExchange", function () {
  let StockExchange;
  let stockExchange;
  let owner;

  beforeEach(async () => {
    StockExchange = await ethers.getContractFactory("StockExchange");
    stockExchange = await StockExchange.deploy();
    [owner] = await ethers.getSigners();
  });

  it("Should create a stock", async () => {
    await stockExchange.createStock("AAPL", ethers.utils.parseEther("150"));
    const stock = await stockExchange.getStock(1);
    expect(stock[0]).to.equal("AAPL");
    expect(stock[1]).to.equal(ethers.utils.parseEther("150"));
    expect(stock[2]).to.equal(owner.address);
  });

  it("Should transfer stock", async () => {
    await stockExchange.createStock("AAPL", ethers.utils.parseEther("150"));
    const [_, addr1] = await ethers.getSigners();
    await stockExchange.transferStock(1, addr1.address);
    const stock = await stockExchange.getStock(1);
    expect(stock[2]).to.equal(addr1.address);
  });
});

