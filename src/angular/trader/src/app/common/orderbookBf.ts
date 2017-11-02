export interface OrderbookBf {
    bids: OrderBf[];
    asks: OrderBf[];
}

export interface OrderBf {
    price: string;
    amount: string;
    timestamp: Date;
}