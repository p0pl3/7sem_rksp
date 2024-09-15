// SPDX-License-Identifier: UNLICENSED


pragma solidity ^0.8.0;

contract StockExchange {
    struct Stock {
        string name;
        uint256 price;
        address owner;
    }

    mapping(uint256 => Stock) public stocks;
    uint256 public stockCount;

    event StockCreated(uint256 id, string name, uint256 price, address owner);
    event StockTransferred(uint256 id, address from, address to);

    function createStock(string memory _name, uint256 _price) public {
        stockCount++;
        stocks[stockCount] = Stock(_name, _price, msg.sender);
        emit StockCreated(stockCount, _name, _price, msg.sender);
    }

    function transferStock(uint256 _id, address _to) public {
        require(stocks[_id].owner == msg.sender, "You are not the owner of this stock");
        stocks[_id].owner = _to;
        emit StockTransferred(_id, msg.sender, _to);
    }

    function getStock(uint256 _id) public view returns (string memory, uint256, address) {
        Stock memory stock = stocks[_id];
        return (stock.name, stock.price, stock.owner);
    }
}
