const PROXY_CONFIG = [
    {
        context: [
            "/bitstamp",
            "/coinbase",
            "/itbit",
            "/bitfinex",
            "/myuser",
            "/statistics"
        ],
        target: "http://localhost:8080",
        secure: false
    }
]

module.exports = PROXY_CONFIG;