import json
import time
import uuid

import requests
from prettytable import PrettyTable

IP = "http://localhost"
PORT = ":8080"
HEADERS = {"Content-Type": "application/json"}
REGISTER_URL = IP + PORT + "/connectfive/register/"
CHECK_TURN_URL = IP + PORT + "/connectfive/checkturn"
MAKE_MOVE_URL = IP + PORT + "/connectfive/move"
DISCONNECT_URL = IP + PORT + "/connectfive/disconnect"

# TODO : Extract this from the board
ROW_COUNT = 6
COLUMN_COUNT = 9


def rest_client(method, url, payload, headers=HEADERS):
    rest_response = requests.request(method, url, headers=headers, data=payload)
    return rest_response


def register_player():
    url = REGISTER_URL + player_name
    payload = {
        "name": player_name,
        "playerId": player_id
    }
    registration_response = rest_client("POST", url, json.dumps(payload))
    print(registration_response.json())
    return registration_response.json()


def pretty_print_board(board_to_print):
    reversed_board = reversed(board_to_print)
    # Reverse the view so that the pieces look like they are dropping

    p = PrettyTable()
    for row in reversed_board:
        p.add_row(row)
    print(p.get_string(header=False, border=True))


def check_turn():
    payload = {
        "boardId": board_id,
        "playerId": player_id
    }
    turn_response = rest_client("GET", CHECK_TURN_URL, json.dumps(payload))
    # print(turn_response.json())
    return turn_response.json()


def process_player_move(column):
    payload = {
        "boardId": board_id,
        "playerId": player_id,
        "move": column - 1
    }
    move_response = rest_client("POST", MAKE_MOVE_URL, json.dumps(payload))
    # print(move_response.json())
    return move_response.json()


def verify_move_validity(input_move):
    if (1 <= input_move <= COLUMN_COUNT) and board[ROW_COUNT-1][input_move-1] == '_':
        return False
    else:
        print("Invalid input value entered. Please choose an unoccupied space")
        return True


def get_move():
    invalid_move = True
    while invalid_move:
        try:
            input_move = int(input(
                "It’s your turn {0}, please enter column (1-{1}):".format(player_name, str(COLUMN_COUNT))))
            invalid_move = verify_move_validity(input_move)
        except ValueError:
            print("Please enter only numbers")
    return input_move


def send_disconnect():
    payload = {
        "boardId": board_id,
        "playerId": player_id
    }
    rest_client("DELETE", DISCONNECT_URL, json.dumps(payload))


player_name = input("Hello Player! Enter your name : ")
print("You\'ve entered {0}".format(str(player_name)))
print("Please wait while we find you a board ")

player_id = str(uuid.uuid4())
game_board = register_player()

board_id = game_board["boardId"]
pretty_print_board(game_board["board"])
game_over = False

try:
    while not game_over:
        response = check_turn()
        if response["playerTurn"]:
            board = response["board"]
            pretty_print_board(response["board"])
            response = process_player_move(get_move())
            print("Waiting for opponents move")
            pretty_print_board(response["board"])
        elif response["state"] == "GAME_WON":
            if response["winner"] == player_id:
                print("Congratulations {0}!!! You WON!!".format(player_name))
            else:
                print("Oh No {0}! You Lost!! Try Again".format(player_name))
            game_over = True
        elif response["state"] == "GAME_TIED":
            game_over = True
            print("Game Tied. Better luck next time")
        elif response["state"] != "GAME_IN_PROGRESS":
            print("Waiting for opponent to join...")
        else:
            print("Waiting for other player's move")
        time.sleep(3)
except KeyboardInterrupt:
    print("Interrupted by player")
    send_disconnect()

# TODO: ClientSide Validation
# Disconnect
# test cases
