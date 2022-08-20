cd ~/.stoready/db

echo "Create recovery SQL script"
$(which java) -cp ~/.m2/repository/com/h2database/h2/2.1.212/h2-2.1.212.jar org.h2.tools.Recover

echo "Create temp folder"
mkdir -p ~/.stoready/db/temp

echo "Move corrupted db to temp folder"
mv stoready.mv.db stoready.trace.db ~/.stoready/db/temp

echo "Execute recovery SQL script"
java -cp ~/.m2/repository/com/h2database/h2/2.1.212/h2-2.1.212.jar org.h2.tools.RunScript \
-url "jdbc:h2:~/.stoready/db/stoready;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_ON_EXIT=FALSE" \
-script stoready.h2.sql

echo "Database recovery done"
