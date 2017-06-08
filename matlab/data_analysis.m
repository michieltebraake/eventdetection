use_standard_axis = true;
%WARNING: using add_ticks may cause MATLAB to be extremely slow if you have
%many values
add_ticks = false;
%Controls if events should be drawn (this needs to be output from the java
%program)
draw_events = true;
%Controls if the original vector is also drawn
draw_both_vectors = false;
%Actual number of seconds of the measurement
seconds_measured = 511;

%Make sure the mean is 0
Y = Y - mean(Y);

%Create vector
%mean_points = 39;
mean_points = 39;
sliding_mean_Y = conv(Y, ones(mean_points,1)/mean_points, 'same');

%Make sure jumping mean matrix is divisible by 20
jumping_mean_Y = [zeros(20 - mod(length(Y), 20),1);sliding_mean_Y];
%Reshape it
jumping_mean_Y = mean(reshape(jumping_mean_Y,20,[]))';

%Create an x axis with seconds as values
x_axis = linspace(0, seconds_measured, length(Y));

% Draw events
if draw_events
    hold on;
    for index = 1:numel(i)
        i_loc = i(index);
        j_loc = j(index);
        r = rectangle('Position', [i_loc, -2, j_loc, 4], 'FaceColor', [1 0 1]);
    end
    hold off;
end

%Draw plot
hold on;
if use_standard_axis
    if draw_both_vectors
        plot(Y,'DisplayName','Y');
        hold on;
    end
    plot(sliding_mean_Y,'DisplayName','sliding_mean_Y')
    if draw_both_vectors
        %hold off;
    end
else
    if draw_both_vectors
        plot(x_axis, Y,'DisplayName','Y');
        hold on;
    end
    plot(x_axis, sliding_mean_Y,'DisplayName','sliding_mean_Y')
    if draw_both_vectors
        %hold off;
    end
end
hold off;

%Add horizontal reference line
line(xlim,[0 0],'Color','k');


% OPTIONALLY add a ton of xticks
if add_ticks
    if use_standard_axis
        xticks(0:50:length(Y))
        xtickangle(90)
    else
        xticks(0:2:seconds_measured)
        xtickangle(90)
    end
end