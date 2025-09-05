CXX = g++
CXXFLAGS = -std=c++17 -I./includes -Wall -Wextra -Werror

SRCS = $(wildcard *.cpp)
OBJS = $(SRCS:.cpp=.o)
NAME = test_app

all: $(NAME)

$(NAME): $(OBJS)
	$(CXX) $(CXXFLAGS) -o $@ $^

%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $< -o $@

clean:
	rm -f $(OBJS)

fclean: clean
	rm -f $(NAME)

re: fclean all

.PHONY: all clean fclean re