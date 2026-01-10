import sys
import socket

def main():
    if sys.argv.__len__() <= 3:
        print("Usage: python3 sender.py [host] [port] [messages]...")
        return

    encoding = "ascii"
    host = sys.argv[1]
    port = int(sys.argv[2])

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect((host, port))

        for arg in sys.argv[3:]:
            s.send(arg.encode(encoding=encoding))


if __name__ == "__main__":
    main()