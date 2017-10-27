export interface OrderbookBf {
    bids: Order[];
    asks: Order[];
}

export interface Order {
    price: number;
    amount: number;
    timestamp: Date;
}