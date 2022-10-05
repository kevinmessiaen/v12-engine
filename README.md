# Messik V12 engine

An Engine to find the best trading algorithm by running simulation on past data

## How it works

The engine parse historical data saved on a CSV and will simulate longing, shorting and might even use leverage in order to find the best algorithm

It will start by creating different small periods and generate random algorithms based on predefined rules. It will then select the best algorithms and perform genetic mutation as well as generating new ones.

It will repeat the step several times and move on the next period. Once finish it will run the algo on the whole dataset and select the best algo.

This method will most probably result in an overfitted algo, and it is not advised to use any generated algo without performing a walk forward test.

## How to use it

For now it needs modification in the code to run custom simulation, you can still run the default simulation by compiling and running the main class `com.messik.v12.V12`

I am planning to add CLI support in the future

## Does it work

In theory, it does, it can find algorithms with 631647% ROI over 3 years (the score is the ROI)

However, it doesn't take into account the maximum draw down and the sharpe ratio that are very important.

Furthermore, past data does not represent future data, the market always change and external event might affect the price.

### Disclaimer

Use at your own risk

The name is inspired by the famous JS engine
