# Contributing guidelines
* Fork and Clone the repository.<br />
* Add this as remote upstream.<br />
* Create new branch for features.<br />
* Run the all tests.<br />
* Pull rebase latest changes from upstream before pushing your code or create a new feature branch.<br />
* Send a PR to this repo for review and merging.

# NOTE:
Never push directly to main repository (upstream).  
Only push to your forked repo (origin) and send a pull request to the main repository

# commit message format
Each commit message consists of a header, a body and a footer. The header has a special format that includes a type, a scope and a subject:
```
<type>(<scope>): <subject>
<BLANK LINE>
<body>
```
Any line of the commit message cannot be longer 100 characters.
Example Commit Message
```  
feat(Crop): implement SquareCrop

squarecropping feature is added (Cropping_Fragment.kt)
```
